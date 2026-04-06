#!/usr/bin/env bash
# Verify that relative file-path links in markdown docs resolve to real files.
# Checks links of the form [text](relative/path) and [text](relative/path#anchor).
# Skips URLs (http://, https://) and pure anchors (#heading).
set -uo pipefail

REPO_ROOT="$(git rev-parse --show-toplevel)"
ERRORS_FILE="$(mktemp)"
trap 'rm -f "$ERRORS_FILE"' EXIT

# Collect all markdown files to check
mapfile -t md_files < <(find "$REPO_ROOT/docs" -name '*.md' 2>/dev/null)
[ -f "$REPO_ROOT/README.md" ] && md_files+=("$REPO_ROOT/README.md")

for md_file in "${md_files[@]}"; do
    dir="$(dirname "$md_file")"

    # Extract all markdown link targets: [text](target)
    targets="$(grep -oE '\[[^]]*\]\([^)]+\)' "$md_file" 2>/dev/null | sed 's/.*](\(.*\))/\1/' || true)"
    [ -z "$targets" ] && continue

    while IFS= read -r target; do
        # Skip URLs and pure anchors
        case "$target" in
            http://*|https://*) continue ;;
            \#*) continue ;;
        esac

        # Strip anchor fragment
        filepath="${target%%#*}"
        [ -z "$filepath" ] && continue

        # Resolve relative to the markdown file's directory
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
