#!/bin/bash
# Format code using ktlint
# Similar to npm run format in JS projects

set -e

echo "💅 Formatting code with ktlint..."

./gradlew ktlintFormat

echo "✅ Code formatting complete!"

