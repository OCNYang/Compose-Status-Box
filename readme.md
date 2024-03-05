# Compose StatusBox

Help you easily handle the different states of the page: No data, Successful, Failed, Loading...

## Preview

![Preveiw](https://cdn.jsdelivr.net/gh/ocnyang/compose-status-box/master/res/preview.gif)

## Gradle Dependency

```groovy
dependencies {
    implementation 'com.github.ocnyang:compose-status-box:1.0.0'
}
```
[[More dependent information]](https://jitpack.io/#OCNYang/compose-status-box)


## Usage

```kotlin
@Composable
fun MainPage() {
    val stateContainer = StateContainer<String>(state = UIState.Initial, loadingState = true to "")
    ...
    StatusBox(
            modifier = Modifier.fillMaxSize(),
            stateContainer = stateContainer,
            contentScrollEnabled = true,
            loadingComponentBlock = { InstaSpinner(size = 25.dp) } 
    ) {
        ...
    }
}
```

You can set the global configuration：  

```kotlin
class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        // You can use the built-in default configuration
        // StatusBoxGlobalConfig.initDef()

        // Or you can set up a custom page in this way
        StatusBoxGlobalConfig.apply {
            errorComponent { DefaultErrorStateView() }
            loadingComponent { DefaultLoadingStateView() }
            emptyComponent { DefaultEmptyStateView() }
            initComponent { DefaultInitialStateView() }
        }
    }
}
```

> For more detailed usage, see Demo or view the source code.