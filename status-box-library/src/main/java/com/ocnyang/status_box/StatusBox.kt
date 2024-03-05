package com.ocnyang.status_box

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun <T> StatusBox(
    modifier: Modifier = Modifier.fillMaxSize(),
    stateContainer: StateContainer<T>,
    contentScrollEnabled: Boolean = true,
    loadingBlockPress: Boolean = false,
    initComponentBlock: @Composable (BoxScope.(UIState.Initial) -> Unit)? = StatusBoxGlobalConfig.initComponent,
    emptyComponentBlock: @Composable (BoxScope.(UIState.Empty) -> Unit)? = StatusBoxGlobalConfig.emptyComponent,
    errorComponentBlock: @Composable (BoxScope.(UIState.Error) -> Unit)? = StatusBoxGlobalConfig.errorComponent,
    loadingComponentBlock: @Composable (BoxScope.(Pair<Boolean, Any?>) -> Unit)? = StatusBoxGlobalConfig.loadingComponent,
    contentComponentBlock: @Composable (ColumnScope.(UIState.Success<T>) -> Unit)
) {
    val uiState by stateContainer.uiStateFlow.collectAsState()
    val loading by stateContainer.loadingStateFlow.collectAsState()

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when (uiState) {
            is UIState.Success ->
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState(), enabled = contentScrollEnabled)
                ) {
                    this.contentComponentBlock(uiState as UIState.Success<T>)
                }

            is UIState.Error -> errorComponentBlock?.invoke(this, uiState as UIState.Error)
            is UIState.Empty -> emptyComponentBlock?.invoke(this, uiState as UIState.Empty)
            is UIState.Initial -> initComponentBlock?.invoke(this, uiState as UIState.Initial)
        }

        if (loading.first) {
            if (loadingBlockPress) {
                Box(
                    modifier = Modifier.fillMaxSize().pointerInput(Unit) {},
                    contentAlignment = Alignment.Center
                ) {
                    loadingComponentBlock?.invoke(this, loading)
                }
            } else {
                loadingComponentBlock?.invoke(this, loading)
            }
        }
    }
}





