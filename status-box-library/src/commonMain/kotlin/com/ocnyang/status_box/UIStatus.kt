package com.ocnyang.status_box

interface UIState {
    data object Initial : UIState

    data class Error(val message: String, val throwable: Throwable? = null) : UIState

    data class Empty(val value: Any? = null) : UIState
}