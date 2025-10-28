#!/bin/bash
# Lint script for Ticktzy project
# Similar to npm run lint in JS projects

set -e

echo "🔍 Running Kotlin linter (ktlint)..."

./gradlew ktlintCheck

echo "✅ ktlint check complete!"

