package com.ocnyang.demo

import com.ocnyang.status_box.UIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Cross-platform ViewModel for demo
 * Works on Android, Desktop, and Web
 */
class DemoViewModel {
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _uiState = MutableStateFlow<UIState>(UIState.Initial)
    val uiState = _uiState.asStateFlow()

    private val _loadingState = MutableStateFlow(true to null as Any?)
    val loadingState = _loadingState.asStateFlow()

    private val _itemNumber = MutableStateFlow(5)
    val itemNumber = _itemNumber.asStateFlow()

    init {
        loadData()
    }

    fun changeItemNumber(num: Int) {
        _itemNumber.value = num.coerceAtLeast(0)
    }

    fun changeState(state: UIState) {
        _uiState.value = state
        _loadingState.value = false to null
    }

    fun changeLoading(visible: Boolean, extra: Any? = null) {
        _loadingState.value = visible to extra
    }

    fun loadData() {
        scope.launch {
            _loadingState.value = true to null
            delay(2000)
            _uiState.value = DemoUIState.Success("StatusBox KMP Demo")
            _loadingState.value = false to null
        }
    }

    fun reload() {
        _uiState.value = UIState.Initial
        loadData()
    }
}
