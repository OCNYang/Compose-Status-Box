#!/bin/bash

# GitHub Pages Deployment Script for StatusBox Demo
# This script builds the production JS bundle and copies it to docs/ for GitHub Pages

set -e

echo "🚀 Building production JS bundle..."
./gradlew :composeApp:jsBrowserProductionWebpack

echo "📦 Copying files to docs/..."
mkdir -p docs
cp composeApp/build/kotlin-webpack/js/productionExecutable/composeApp.js docs/
cp composeApp/build/processedResources/js/main/skiko.js docs/
cp composeApp/build/processedResources/js/main/skiko.wasm docs/

echo "✅ Build complete!"
echo ""
echo "📋 Next steps:"
echo "1. Commit the docs/ directory to your repository"
echo "2. Push to GitHub"
echo "3. Enable GitHub Pages in repository settings:"
echo "   - Go to Settings > Pages"
echo "   - Source: Deploy from a branch"
echo "   - Branch: master (or main)"
echo "   - Folder: /docs"
echo "4. Your demo will be available at: https://YOUR_USERNAME.github.io/YOUR_REPO/"
echo ""
echo "📊 File sizes:"
ls -lh docs/composeApp.js docs/skiko.js docs/skiko.wasm 2>/dev/null || ls -lh docs/*.js docs/*.wasm
