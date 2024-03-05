package com.ocnyang.status_box

sealed interface UIState<out T> {
    data object Initial : UIState<Nothing>

    data class Success<T>(val data: T) : UIState<T>

    data class Error(val message: String, val throwable: Throwable? = null) : UIState<Nothing>


    data class Empty(val value: Any? = null) : UIState<Nothing>
}