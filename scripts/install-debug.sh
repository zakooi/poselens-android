#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "Checking for adb..."
if ! command -v adb >/dev/null 2>&1; then
  echo "adb not found in PATH. Please install Android Platform Tools and add adb to PATH." >&2
  exit 1
fi

echo "Checking connected devices..."
devices_count=$(adb devices | awk 'NR>1 && $2=="device" {count++} END {print count+0}')
if [ "$devices_count" -eq 0 ]; then
  echo "No connected devices detected. Please connect your device and enable USB debugging." >&2
  adb devices
  exit 2
fi

echo "Building debug APK..."
./gradlew installDebug

echo "Done. App should be installed on connected device(s)."
