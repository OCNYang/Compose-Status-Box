package com.ocnyang.status_box

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class StateContainer<T>(
    state: UIState<T>,
    loadingState: Pair<Boolean, Any?> = Pair(false, null)
) {
    private val _uiStateFlow = MutableStateFlow<UIState<T>>(state)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _loadingStateFlow = MutableStateFlow(loadingState)
    val loadingStateFlow = _loadingStateFlow.asStateFlow()

    fun changeState(state: UIState<T>, loading: Pair<Boolean, Any?> = false to null) {
        _uiStateFlow.value = state
        _loadingStateFlow.value = loading
    }

    fun changeLoading(
        visible: Boolean,
        extra: Any? = null
    ) {
        _loadingStateFlow.value = visible to extra
    }
}