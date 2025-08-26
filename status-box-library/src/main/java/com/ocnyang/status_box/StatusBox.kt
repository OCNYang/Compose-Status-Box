package com.ocnyang.status_box

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun StatusBox(
    modifier: Modifier = Modifier,
    uiState: UIState,
    loadingState: Pair<Boolean, Any?>,
    loadingBlockPress: Boolean = false,
    initComponentBlock: @Composable (BoxScope.(UIState.Initial) -> Unit)? = StatusBoxGlobalConfig.initComponent,
    emptyComponentBlock: @Composable (BoxScope.(UIState.Empty) -> Unit)? = StatusBoxGlobalConfig.emptyComponent,
    errorComponentBlock: @Composable (BoxScope.(UIState.Error) -> Unit)? = StatusBoxGlobalConfig.errorComponent,
    loadingComponentBlock: @Composable (BoxScope.(Pair<Boolean, Any?>) -> Unit)? = StatusBoxGlobalConfig.loadingComponent,
    contentComponentBlock: @Composable ((UIState) -> Unit)
) {
    StatusBoxContent(
        modifier = modifier,
        uiState = uiState,
        errorComponentBlock = errorComponentBlock,
        emptyComponentBlock = emptyComponentBlock,
        initComponentBlock = initComponentBlock,
        contentComponentBlock = contentComponentBlock,
        loadingState = loadingState,
        loadingBlockPress = loadingBlockPress,
        loadingComponentBlock = loadingComponentBlock
    )
}

@Composable
fun <T : UIState> StatusBox(
    modifier: Modifier = Modifier,
    uiState: UIState,
    loadingState: Pair<Boolean, Any?>,
    loadingBlockPress: Boolean = false,
    successStateTransformFun: ((UIState) -> T) = { it as T },
    initComponentBlock: @Composable (BoxScope.(UIState.Initial) -> Unit)? = StatusBoxGlobalConfig.initComponent,
    emptyComponentBlock: @Composable (BoxScope.(UIState.Empty) -> Unit)? = StatusBoxGlobalConfig.emptyComponent,
    errorComponentBlock: @Composable (BoxScope.(UIState.Error) -> Unit)? = StatusBoxGlobalConfig.errorComponent,
    loadingComponentBlock: @Composable (BoxScope.(Pair<Boolean, Any?>) -> Unit)? = StatusBoxGlobalConfig.loadingComponent,
    contentComponentBlock: @Composable ((T) -> Unit)
) {
    StatusBoxContent(
        modifier = modifier,
        uiState = uiState,
        errorComponentBlock = errorComponentBlock,
        emptyComponentBlock = emptyComponentBlock,
        initComponentBlock = initComponentBlock,
        contentComponentBlock = { contentComponentBlock(successStateTransformFun(it)) },
        loadingState = loadingState,
        loadingBlockPress = loadingBlockPress,
        loadingComponentBlock = loadingComponentBlock
    )
}

@Composable
private fun StatusBoxContent(
    modifier: Modifier,
    uiState: UIState,
    errorComponentBlock: @Composable (BoxScope.(UIState.Error) -> Unit)?,
    emptyComponentBlock: @Composable (BoxScope.(UIState.Empty) -> Unit)?,
    initComponentBlock: @Composable (BoxScope.(UIState.Initial) -> Unit)?,
    contentComponentBlock: @Composable ((UIState) -> Unit),
    loadingState: Pair<Boolean, Any?>,
    loadingBlockPress: Boolean,
    loadingComponentBlock: @Composable (BoxScope.(Pair<Boolean, Any?>) -> Unit)?
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when (uiState) {
            is UIState.Error -> errorComponentBlock?.invoke(this, uiState)
            is UIState.Empty -> emptyComponentBlock?.invoke(this, uiState)
            is UIState.Initial -> initComponentBlock?.invoke(this, uiState)
            else -> contentComponentBlock.invoke(uiState)
        }

        AnimatedVisibility(visible = loadingState.first, modifier = Modifier.fillMaxSize()) {
            val loadingModifier = remember(loadingBlockPress) {
                if (loadingBlockPress)
                    Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {}
                else
                    Modifier.fillMaxSize()
            }

            Box(
                modifier = loadingModifier,
                contentAlignment = Alignment.Center
            ) {
                loadingComponentBlock?.invoke(this, loadingState)
            }

        }
    }
}





