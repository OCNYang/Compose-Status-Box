package com.ocnyang.compose_status_page_demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ocnyang.status_box.UIState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Initial)
        private set

    var loadingState: MutableStateFlow<Pair<Boolean, Any?>> = MutableStateFlow(true to null)
        private set

    private val _itemNumber = MutableStateFlow(1)
    var itemNumber = _itemNumber.asStateFlow()

    fun  changeItemNumber(num: Int) {
        _itemNumber.value = num
    }

    fun getData() {
        viewModelScope.launch {
            delay(2000)
            uiState.value = MainUIState.Success(1, "Hello World")
        }
    }

    fun changeState(state: UIState) {
        uiState.value = state
        changeLoading(false, null)
    }

    fun changeLoading(visible: Boolean, extra: Any? = null) {
        loadingState.value = visible to extra
    }


}

class MainUIState : UIState {
    data class Success(
        val id: Int,
        val data: String
    ) : UIState
}