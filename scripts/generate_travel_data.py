"""Compatibility wrapper for the unified offline data generator.

Purpose: preserve the old script entrypoint while using the merged generator.
Input: command-line args forwarded to generate_merged_listings.py.
Returns: process exit status from the merged generator.
"""

import subprocess
import sys
from pathlib import Path


def main() -> int:
    """Forward execution to scripts/generate_merged_listings.py.

    Input: CLI args passed to this wrapper.
    Returns: exit code from the merged generator process.
    """
    script_path = Path(__file__).resolve().parent / "generate_merged_listings.py"
    command = [sys.executable, str(script_path), *sys.argv[1:]]
    print("generate_travel_data.py is deprecated. Running unified generator instead...")
    completed = subprocess.run(command, check=False)
    return completed.returncode


if __name__ == "__main__":
    raise SystemExit(main())
