package com.ocnyang.status_box

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable

/**
 * If you want to configure globally, you just need to look like this: [StatusBoxGlobalConfig.init]
 */
object StatusBoxGlobalConfig {
    internal var initComponent: @Composable (BoxScope.(UIState.Initial) -> Unit)? = {DefaultInitialStateView()}
    internal var emptyComponent: @Composable (BoxScope.(UIState.Empty) -> Unit)? = { DefaultEmptyStateView()}
    internal var errorComponent: @Composable (BoxScope.(UIState.Error) -> Unit)? = { DefaultErrorStateView() }
    internal var loadingComponent: @Composable (BoxScope.(Pair<Boolean, Any?>) -> Unit)? = { DefaultLoadingStateView() }

    fun loadingComponent(component: @Composable (BoxScope.(Pair<Boolean, Any?>) -> Unit)? = null) {
        loadingComponent = component
    }

    fun initComponent(component: @Composable (BoxScope.(UIState.Initial) -> Unit)? = null) {
        initComponent = component
    }

    fun errorComponent(component: @Composable (BoxScope.(UIState.Error) -> Unit)? = null) {
        errorComponent = component
    }

    fun emptyComponent(component: @Composable (BoxScope.(UIState.Empty) -> Unit)? = null) {
        emptyComponent = component
    }
}