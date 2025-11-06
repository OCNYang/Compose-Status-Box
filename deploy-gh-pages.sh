#!/bin/bash

# GitHub Pages Deployment Script for StatusBox Demo (WebAssembly)
# This script builds the production WasmJS bundle and copies it to docs/ for GitHub Pages

set -e

echo "🚀 Building production WebAssembly bundle..."
./gradlew :composeApp:wasmJsBrowserProductionWebpack

echo "📦 Copying files to docs/..."
mkdir -p docs
cp composeApp/build/kotlin-webpack/wasmJs/productionExecutable/* docs/

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
ls -lh docs/composeApp.js docs/*.wasm 2>/dev/null || echo "Build files ready in docs/"
