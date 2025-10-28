#!/bin/bash
# Auto-fix code style issues
# Similar to npm run fix in JS projects

set -e

echo "🔧 Auto-fixing code style issues..."

# Format code
echo "💅 Formatting code..."
./gradlew ktlintFormat

echo "✅ Code style auto-fix complete!"

