# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Compose StatusBox** is a Kotlin Multiplatform (KMP) Jetpack Compose library that simplifies handling different UI states (Initial, Loading, Success, Error, Empty) with a declarative composable wrapper. Published via JitPack at `com.github.ocnyang:compose-status-box-kmp`.

## Project Structure

- **app/**: Android demo application showcasing the library usage
  - `MainActivity.kt`: Main demo with navigation, state management examples
  - `MainViewModel.kt`: ViewModel demonstrating state transitions and loading control
- **status-box-library/**: Core KMP library module (published to JitPack)
  - `commonMain/`: Cross-platform code
    - `kotlin/`: Shared Kotlin code
      - `StatusBox.kt`: Main composable component with two overloaded variants
      - `UIStatus.kt`: Sealed interface defining state types (Initial, Error, Empty)
      - `StatusBoxGlobalConfig.kt`: Global configuration for default state components
      - `DefaultStatusWidget.kt`: Built-in default UI components for each state
    - `composeResources/`: Cross-platform resources (drawables, strings)
  - `androidMain/`: Android-specific code (Preview annotations only)
  - `iosMain/`: iOS-specific code (currently empty)
  - `desktopMain/`: Desktop-specific code (currently empty)
  - `wasmJsMain/`: Web (Wasm) specific code (currently empty)
  - `jsMain/`: Web (JS) specific code (currently empty)

## Build and Test Commands

### Build
```bash
# Build the entire project (Android + Desktop + iOS + Web)
./gradlew build

# Build library for Android only
./gradlew :status-box-library:assembleDebug :status-box-library:assembleRelease

# Build desktop JAR
./gradlew :status-box-library:desktopJar

# Build iOS framework (requires macOS with Xcode)
./gradlew :status-box-library:linkDebugFrameworkIosArm64

# Build Web (Wasm) klib
./gradlew :status-box-library:wasmJsJar

# Build Web (JS) klib
./gradlew :status-box-library:jsJar

# Build demo app
./gradlew :app:assembleDebug

# Build composeApp demo (Desktop + Web)
./gradlew :composeApp:build
```

### Run Tests
```bash
# Run all tests
./gradlew test

# Run library tests for specific platform
./gradlew :status-box-library:testDebugUnitTest      # Android
./gradlew :status-box-library:desktopTest            # Desktop
./gradlew :status-box-library:iosSimulatorArm64Test  # iOS Simulator
./gradlew :status-box-library:wasmJsTest             # Web (Wasm)
./gradlew :status-box-library:jsTest                 # Web (JS)
```

### Publishing
```bash
# Publish to local Maven repository for testing
./gradlew :status-box-library:publishToMavenLocal

# The library publishes to JitPack automatically when tagged
# Tag format: v2.0.0 (for KMP version)
```

## Core Architecture

### Kotlin Multiplatform Setup

The library is a KMP module supporting:
- **Android** (minSdk 24, targetSdk 35) - AAR output
- **iOS** (arm64, x64, simulatorArm64) - Framework output
- **Desktop** (JVM) - JAR output
- **Web (Wasm)** - KLIB output for modern browsers (Chrome 119+, Firefox 120+, Safari 17.4+)
- **Web (JS)** - KLIB output for legacy browser compatibility
- Resources managed via **Compose Multiplatform Resources**

### UIState System

The library uses a sealed `UIState` interface with three built-in states:
- `UIState.Initial`: Initial state before any data loading
- `UIState.Error(message, throwable?)`: Error state with message and optional exception
- `UIState.Empty(value?)`: Empty/no-data state with optional metadata

Custom success states can be defined by implementing `UIState` (see `MainUIState.Success` in the demo).

### StatusBox Component

Two overloaded `StatusBox` composable variants:

1. **Generic variant**: Accepts any `UIState`, content receives base `UIState`
2. **Type-safe variant**: Uses `successStateTransformFun` to transform `UIState` to specific type `T`, ensuring content block receives strongly-typed success state

Both variants accept:
- `uiState`: Current UI state
- `loadingState`: Pair<Boolean, Any?> for loading visibility + optional extra data
- `loadingBlockPress`: Whether loading overlay blocks user interaction (default: false)
- Component blocks for each state (falls back to `StatusBoxGlobalConfig` if not provided)

### Loading State Pattern

Loading is handled separately from UIState via a `Pair<Boolean, Any?>`:
- First element: loading visibility
- Second element: optional extra data (e.g., progress percentage, loading message)

This allows loading overlays on any state, not just success states.

### Global Configuration

Use `StatusBoxGlobalConfig` in Application.onCreate() to set default components for all states:
```kotlin
StatusBoxGlobalConfig.apply {
    initComponent { /* custom initial view */ }
    emptyComponent { /* custom empty view */ }
    errorComponent { /* custom error view */ }
    loadingComponent { /* custom loading view */ }
}
```

Individual `StatusBox` instances can override these with their own component blocks.

### Compose Multiplatform Resources

Resources are managed using Compose Resources API:
- **Location**: `src/commonMain/composeResources/`
- **Generated class**: `com.ocnyang.status_box.resources.Res`
- **Usage example**:
  ```kotlin
  import com.ocnyang.status_box.resources.*
  import org.jetbrains.compose.resources.painterResource
  import org.jetbrains.compose.resources.stringResource

  val text = stringResource(Res.string.hint_empty)
  val icon = painterResource(Res.drawable.ic_empty)
  ```

**Important**: When customizing DefaultStatusWidget components:
- Use `Painter?` type instead of `Int` for icons
- Pass `painterResource(Res.drawable.xxx)` instead of `R.drawable.xxx`
- Use `stringResource(Res.string.xxx)` for strings

## Key Dependencies

- **Kotlin**: 2.2.0
- **Compose Multiplatform**: 1.7.1
- **Jetpack Compose BOM**: 2025.08.00
- **AGP**: 8.11.0
- **Gradle**: 8.14.2
- **Target SDK**: 35, Min SDK: 24
- **Java**: 17

## Development Notes

### KMP-Specific Patterns

1. **Source sets**:
   - `commonMain`: Cross-platform code (use this for all business logic)
   - `androidMain`: Android-specific (currently only Preview functions)
   - `iosMain`, `desktopMain`: Platform-specific code when needed

2. **Resources**: All resources live in `commonMain/composeResources/` and are accessible across all platforms

3. **Preview annotations**: `@Preview` is Android-only, so preview functions are in `androidMain/`

4. **API changes from v1.x**:
   - DefaultStatusWidget functions now accept `Painter?` instead of `Int` for icons
   - Use Compose Resources API instead of Android Resources

### Common Tasks

- **Adding new resources**: Place in `src/commonMain/composeResources/drawable/` or `/values/`
- **Platform-specific code**: Use `expect`/`actual` declarations if needed (currently not used)
- **Testing new platforms**: Each platform has its own test source set

### iOS Build Requirements

- Requires macOS with Xcode installed
- If iOS build fails with Xcode errors, it doesn't affect Android/Desktop/Web builds
- iOS framework is output as `StatusBoxLibrary.framework`

### Web Platform Support

The library supports both modern and legacy browsers through dual Web targets:

#### Wasm (WebAssembly) - Primary Target

- **Target audience**: Modern browsers
- **Browser requirements**:
  - Chrome/Edge: 119+ (Dec 2023)
  - Firefox: 120+ (Nov 2023)
  - Safari: 17.4+ (Mar 2024, iOS 17.4+)
- **Coverage**: ~85% of global users (as of 2024)
- **Advantages**: Near-native performance, better Kotlin interop
- **Build output**: `status-box-library-wasm-js.klib` (~51KB)

#### JS (JavaScript) - Fallback Target

- **Target audience**: Legacy browser support
- **Browser requirements**: All modern browsers + older versions
- **Coverage**: ~99% of global users
- **Advantages**: Universal compatibility, smaller initial bundle
- **Build output**: `status-box-library-js.klib` (~3.2MB)

#### Usage in Web Projects

```kotlin
// build.gradle.kts (Web application)
kotlin {
    wasmJs {  // or js(IR) for legacy browsers
        browser()
        binaries.executable()
    }

    sourceSets {
        wasmJsMain.dependencies {  // or jsMain
            implementation("com.github.ocnyang:compose-status-box-kmp:2.0.0")
        }
    }
}

// Main.kt
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.ocnyang.status_box.*

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(title = "My App") {
        var uiState by remember { mutableStateOf<UIState>(UIState.Initial) }
        var loading by remember { mutableStateOf(true to null) }

        StatusBox(
            modifier = Modifier.fillMaxSize(),
            uiState = uiState,
            loadingState = loading
        ) {
            // Your success content
            Text("Content loaded!")
        }
    }
}
```

### Migration Notes (v1.x → v2.0)

The library has been migrated from Android-only to Kotlin Multiplatform:
- All core code is now in `commonMain/kotlin/`
- Resources moved from `src/main/res/` to `src/commonMain/composeResources/`
- Old `src/main/java/` directory removed
- API remains largely compatible, with minor changes to resource handling

## composeApp Module - Web & Desktop Demo

The `composeApp` module is a multiplatform demo application showcasing StatusBox functionality on Desktop (JVM) and Web (Wasm + JS).

### Running the Demo

```bash
# Desktop Demo
./gradlew :composeApp:run

# Web Demo (Wasm - recommended for modern browsers)
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
# Opens at http://localhost:8080/

# Web Demo (JS - legacy browser fallback)
./gradlew :composeApp:jsBrowserDevelopmentRun
# Opens at http://localhost:8080/
```

### Troubleshooting Web Build

**Issue**: `Could not find org.nodejs:node:22.0.0` or repository configuration errors

**Solution**: The project uses `PREFER_PROJECT` repository mode to allow Kotlin/JS to download Node.js from its official distribution server. This is configured in:
- `settings.gradle.kts`: `repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)`
- `build.gradle.kts`: `allprojects { repositories { ... } }`

If you encounter repository-related errors:
1. Ensure `settings.gradle.kts` has `RepositoriesMode.PREFER_PROJECT` (not `FAIL_ON_PROJECT_REPOS`)
2. Run `./gradlew clean` to refresh configuration
3. Re-run the Web dev server command

### Demo Features

The composeApp demo includes:
- Interactive state switching (Initial, Loading, Success, Error, Empty)
- Dynamic content counter (+/-)
- Auto-load demonstration
- Material3 design
- 100% shared UI code in `commonMain/kotlin/App.kt`

### Architecture

```
composeApp/
├── src/
│   ├── commonMain/kotlin/App.kt       # 100% shared UI (315 lines)
│   ├── desktopMain/kotlin/Main.kt     # Desktop entry point
│   ├── wasmJsMain/
│   │   ├── kotlin/main.kt             # Wasm entry point
│   │   └── resources/index.html       # Wasm HTML template
│   └── jsMain/
│       ├── kotlin/main.kt             # JS entry point
│       └── resources/index.html       # JS HTML template
└── build.gradle.kts                   # KMP configuration (Desktop + Wasm + JS)
```

## GitHub Pages Deployment

The Web-JS demo can be deployed to GitHub Pages for live demonstration.

### Quick Deploy

```bash
# Run the deployment script
./deploy-gh-pages.sh
```

This script:
1. Builds production JS bundle (optimized, ~1.9MB)
2. Copies files to `docs/` directory
3. Creates necessary GitHub Pages files

### Manual Deployment Steps

```bash
# 1. Build production bundle
./gradlew :composeApp:jsBrowserProductionWebpack

# 2. Copy files to docs/
mkdir -p docs
cp composeApp/build/kotlin-webpack/js/productionExecutable/composeApp.js docs/
cp composeApp/build/processedResources/js/main/skiko.js docs/

# 3. Commit and push
git add docs/
git commit -m "Deploy: Update GitHub Pages demo"
git push origin master
```

### Enable GitHub Pages

1. Go to your repository on GitHub
2. Navigate to **Settings** > **Pages**
3. Under "Source", select:
   - Branch: `master` (or `main`)
   - Folder: `/docs`
4. Click **Save**
5. Your demo will be available at: `https://YOUR_USERNAME.github.io/YOUR_REPO/`

### Files in docs/

```
docs/
├── .nojekyll              # Prevents Jekyll processing
├── README.md              # GitHub Pages info
├── index.html             # Entry page (with GitHub corner link)
├── composeApp.js          # Production JS bundle (~1.9MB, minified)
└── skiko.js               # Skiko rendering engine (~400KB)
```

### Production Build Optimizations

The production build includes:
- **Minification**: Webpack minifies all JS code
- **Tree shaking**: Removes unused code
- **Size reduction**: 26.8MB (dev) → 1.9MB (prod)
- **Browser caching**: Static files with cache headers

### Custom Domain (Optional)

To use a custom domain:
1. Create a `CNAME` file in `docs/`: `echo "demo.yourdomain.com" > docs/CNAME`
2. Configure DNS at your domain provider
3. Update GitHub Pages settings with your custom domain

