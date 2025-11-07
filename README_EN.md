# Compose StatusBox

English | [简体中文](./README.md)

An elegant Kotlin Multiplatform Compose library that helps you easily manage different page states: Initial, Loading, Success, Error, Empty, and more.

[![](https://jitpack.io/v/OCNYang/compose-status-box.svg)](https://jitpack.io/#OCNYang/compose-status-box)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.7.1-blue)](https://github.com/JetBrains/compose-multiplatform)

## ✨ Features

- 🎯 **Declarative UI**: Elegantly manage page states using Compose's declarative syntax
- 🌍 **Full Platform Support**: Android, iOS, Desktop (JVM), Web (Wasm/JS)
- 🎨 **Highly Customizable**: Support for global configuration and local custom state components
- 🔄 **Type Safe**: Ensure type safety through generics and sealed classes
- 🪶 **Lightweight**: Small core library with no extra dependencies
- 📦 **Ready to Use**: Built-in default state views for quick start

## 🎬 Live Demo

**[🚀 Click to View Live Demo](https://ocnyang.github.io/Compose-Status-Box/)**

<p align="center">
  <img src="https://cdn.jsdelivr.net/gh/ocnyang/compose-status-box@master/res/preview.jpg" width="300px" alt="Preview"/>
</p>

## 📦 Installation

### Kotlin Multiplatform (Recommended)

Add JitPack repository in `build.gradle.kts`:

```kotlin
repositories {
    maven("https://jitpack.io")
}
```

Add dependency:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.github.ocnyang:compose-status-box-kmp:2.0.0")
        }
    }
}
```

### Android Only (Legacy)

```groovy
dependencies {
    implementation 'com.github.ocnyang:compose-status-box:1.0.1'
}
```

[![](https://jitpack.io/v/OCNYang/compose-status-box.svg)](https://jitpack.io/#OCNYang/compose-status-box)

## 🚀 Quick Start

### Basic Usage

```kotlin
@Composable
fun MyScreen() {
    var uiState by remember { mutableStateOf<UIState>(UIState.Initial) }
    var loading by remember { mutableStateOf(true to null) }

    StatusBox(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        loadingState = loading
    ) {
        // Your success state content
        Text("Data loaded successfully!")
    }
}
```

### Type-Safe Generic Usage

```kotlin
// Define custom success state
sealed interface MyUIState : UIState {
    data class Success(val data: List<String>) : MyUIState
}

@Composable
fun TypeSafeScreen() {
    var uiState by remember { mutableStateOf<UIState>(UIState.Initial) }
    var loading by remember { mutableStateOf(false to null) }

    StatusBox(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        loadingState = loading,
        successStateTransformFun = { state ->
            if (state is MyUIState.Success) state else null
        }
    ) { successState: MyUIState.Success ->
        // Type-safe: successState is guaranteed to be MyUIState.Success
        LazyColumn {
            items(successState.data) { item ->
                Text(item)
            }
        }
    }
}
```

### Custom State Components

```kotlin
StatusBox(
    uiState = uiState,
    loadingState = loading,
    // Custom initial state
    initComponent = {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Info, contentDescription = null)
            Text("Click button to start loading")
        }
    },
    // Custom error state
    errorComponent = { errorState ->
        Column(
            modifier = Modifier.fillMaxSize().clickable { /* Retry */ },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red)
            Text(errorState.message)
            Button(onClick = { /* Retry logic */ }) {
                Text("Retry")
            }
        }
    }
) {
    // Success content
}
```

### Global Configuration

Configure default state views at app startup:

```kotlin
// Android
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        StatusBoxGlobalConfig.apply {
            initComponent { CustomInitialView() }
            emptyComponent { CustomEmptyView() }
            errorComponent { CustomErrorView(it.message) }
            loadingComponent { CustomLoadingView() }
        }
    }
}

// iOS / Desktop / Web
fun main() {
    StatusBoxGlobalConfig.apply {
        initComponent { CustomInitialView() }
        emptyComponent { CustomEmptyView() }
        errorComponent { CustomErrorView(it.message) }
        loadingComponent { CustomLoadingView() }
    }
    application {
        // Your app startup code
    }
}
```

## 📚 Core Concepts

### UIState

StatusBox uses sealed interface to define three built-in states:

```kotlin
sealed interface UIState {
    object Initial : UIState          // Initial state
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : UIState                       // Error state
    data class Empty(
        val value: Any? = null
    ) : UIState                       // Empty data state
}

// Custom success state (by implementing UIState)
data class Success(val data: YourData) : UIState
```

### LoadingState

Loading state is independent of UIState, represented by `Pair<Boolean, Any?>`:

```kotlin
var loading by remember { mutableStateOf(false to null) }

// Show loading
loading = true to null

// Show loading with progress
loading = true to 0.75  // 75% progress

// Show loading with message
loading = true to "Uploading file..."

// Hide loading
loading = false to null
```

### Two StatusBox Variants

#### 1. Generic Variant (Content receives base UIState)

```kotlin
StatusBox(
    uiState = uiState,
    loadingState = loading
) { baseState: UIState ->
    // Manual type checking required
    if (baseState is MySuccessState) {
        Text(baseState.data)
    }
}
```

#### 2. Type-Safe Variant (Using successStateTransformFun)

```kotlin
StatusBox(
    uiState = uiState,
    loadingState = loading,
    successStateTransformFun = { state ->
        if (state is MySuccessState) state else null
    }
) { successState: MySuccessState ->
    // Automatically type-safe, no type checking needed
    Text(successState.data)
}
```

## 🎨 Customization

### Parameters

```kotlin
StatusBox(
    modifier: Modifier = Modifier,
    uiState: UIState,                                    // Current UI state
    loadingState: Pair<Boolean, Any?> = false to null,  // Loading state with extra data
    loadingBlockPress: Boolean = false,                  // Block clicks during loading
    initComponent: @Composable (() -> Unit)? = null,    // Initial state component
    emptyComponent: @Composable ((UIState.Empty) -> Unit)? = null,  // Empty data component
    errorComponent: @Composable ((UIState.Error) -> Unit)? = null,  // Error component
    loadingComponent: @Composable ((Pair<Boolean, Any?>) -> Unit)? = null,  // Loading component
    successStateTransformFun: ((UIState) -> T?)? = null, // Success state transform function
    content: @Composable (T) -> Unit                     // Success state content
)
```

### Built-in Default Components

StatusBox provides ready-to-use default state views:

- **DefaultInitialStateView**: Blank initial view
- **DefaultEmptyStateView**: Empty data view with icon and hint text
- **DefaultErrorStateView**: Error view with error icon and message
- **DefaultLoadingStateView**: Material Design style loading animation

## 🌍 Platform Support

| Platform | Status | Notes |
|----------|--------|-------|
| Android | ✅ Fully Supported | minSdk 24, targetSdk 35 |
| iOS | ✅ Fully Supported | arm64, x64, simulatorArm64 |
| Desktop (JVM) | ✅ Fully Supported | Windows, macOS, Linux |
| Web (Wasm) | ✅ Fully Supported | Chrome 119+, Firefox 120+, Safari 17.4+ |
| Web (JS) | ✅ Fully Supported | Legacy browser compatibility |

## 🔧 Tech Stack

- **Kotlin**: 2.2.0
- **Compose Multiplatform**: 1.7.1
- **Compose BOM**: 2025.08.00
- **Minimum Requirements**:
  - Android: minSdk 24
  - iOS: iOS 15.0+
  - Desktop: JVM 17+
  - Web: Modern browsers

## 📖 Complete Examples

Check out the complete examples in the `composeApp` module:

```bash
# Run Android example
./gradlew :app:installDebug

# Run Desktop example
./gradlew :composeApp:run

# Run Web example (Wasm)
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Run Web example (JS)
./gradlew :composeApp:jsBrowserDevelopmentRun
```

## 🤝 Contributing

Issues and Pull Requests are welcome!

## 📄 License

```
Copyright 2024 OCNYang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 🔗 Related Links

- [JitPack](https://jitpack.io/#OCNYang/compose-status-box)
- [Live Demo](https://ocnyang.github.io/Compose-Status-Box/)
- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform)
- [Issue Tracker](https://github.com/OCNYang/Compose-Status-Box/issues)

---

If this project helps you, please give it a ⭐️ Star!
