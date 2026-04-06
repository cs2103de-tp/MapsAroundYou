#!/usr/bin/env bash
# Verify that relative markdown links in docs/ and README.md point to files
# that actually exist in the repository.
#
# Note: set -e is intentionally omitted — grep returns exit code 1 when no
# matches are found, which would silently abort the script under errexit.
set -uo pipefail

REPO_ROOT="$(git rev-parse --show-toplevel)"
if [ -z "$REPO_ROOT" ] || [ ! -d "$REPO_ROOT" ]; then
    echo "Failed to determine repository root." >&2
    exit 1
fi

ERRORS_FILE="$(mktemp)"
trap 'rm -f "$ERRORS_FILE"' EXIT

md_files=()
while IFS= read -r -d '' md_file; do
    md_files+=("$md_file")
done < <(find "$REPO_ROOT/docs" -name '*.md' -print0 2>/dev/null)
[ -f "$REPO_ROOT/README.md" ] && md_files+=("$REPO_ROOT/README.md")

for md_file in "${md_files[@]}"; do
    dir="$(dirname "$md_file")"

    # Extract markdown link targets: [text](target)
    targets="$(grep -oE '\[[^]]*\]\([^)]+\)' "$md_file" 2>/dev/null \
              | sed 's/.*](\(.*\))/\1/' || true)"
    [ -z "$targets" ] && continue

    while IFS= read -r target; do
        # Skip external URLs and in-page anchors
        case "$target" in
            http://*|https://*) continue ;;
            \#*) continue ;;
        esac

        # Strip fragment identifier (e.g. file.md#section -> file.md)
        filepath="${target%%#*}"
        [ -z "$filepath" ] && continue

        resolved="$dir/$filepath"
        if [ ! -e "$resolved" ]; then
            rel="${md_file#"$REPO_ROOT"/}"
            echo "  $rel -> $filepath" >> "$ERRORS_FILE"
        fi
    done <<< "$targets"
done

if [ -s "$ERRORS_FILE" ]; then
    echo "Broken doc links found:"
    cat "$ERRORS_FILE"
    echo ""
    echo "Fix the links above or remove them."
    exit 1
fi

echo "All doc links OK."
