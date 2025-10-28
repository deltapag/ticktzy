#!/bin/bash
# Run all code quality checks
# Similar to npm run check in JS projects

set -e

echo "🔍 Running all code quality checks..."

echo "📋 Checking ktlint..."
./gradlew ktlintCheck

echo "🔬 Running detekt..."
./gradlew detekt

echo "✅ All code quality checks passed!"

