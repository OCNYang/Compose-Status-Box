package com.ocnyang.status_box

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

/**
 * Android-specific Preview annotations for StatusBox components
 * These are only available on Android platform
 */

@Preview(showBackground = true)
@Composable
fun PreviewDefaultEmptyStateView() {
    DefaultEmptyStateView()
}

@Preview(showBackground = true)
@Composable
fun PreviewDefaultErrorStateView() {
    DefaultErrorStateView()
}

@Preview(showBackground = true)
@Composable
fun PreviewDefaultLoadingStateView() {
    DefaultLoadingStateView()
}

@Preview(showBackground = true)
@Composable
fun PreviewDefaultInitialStateView() {
    DefaultInitialStateView()
}
