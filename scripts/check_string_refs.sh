#!/usr/bin/env bash
# check_string_refs.sh
# Scans all Kotlin files for R.string.<key> references and verifies each key
# exists in core/resources/src/main/res/values/strings.xml
# Exit code 1 if any references are broken — run in CI before building.

set -euo pipefail

STRINGS_FILE="core/resources/src/main/res/values/strings.xml"
KOTLIN_DIRS="app core feature wearos"
ERRORS=0

if [ ! -f "$STRINGS_FILE" ]; then
  echo "ERROR: strings.xml not found at $STRINGS_FILE"
  exit 1
fi

defined=$(grep -oP 'name="\K[^"]+' "$STRINGS_FILE" | sort -u)
referenced=$(grep -roh --include="*.kt" 'R\.string\.[a-zA-Z0-9_]\+' $KOTLIN_DIRS 2>/dev/null \
  | sed 's/R\.string\.//' | sort -u)

while IFS= read -r key; do
  if ! echo "$defined" | grep -qx "$key"; then
    files=$(grep -rn --include="*.kt" "R\.string\.$key\b" $KOTLIN_DIRS 2>/dev/null | head -5)
    echo "MISSING STRING: R.string.$key"
    echo "$files"
    echo "---"
    ERRORS=$((ERRORS + 1))
  fi
done <<< "$referenced"

if [ "$ERRORS" -gt 0 ]; then
  echo ""
  echo "FAILED: $ERRORS missing string resource(s). Add them to $STRINGS_FILE before building."
  exit 1
else
  echo "OK: All R.string.* references resolve to defined string resources."
fi
