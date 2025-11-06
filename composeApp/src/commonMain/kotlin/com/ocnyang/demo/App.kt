package com.ocnyang.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ocnyang.status_box.StatusBox
import com.ocnyang.status_box.UIState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Main application composable - shared across Desktop and Web
 */
@Composable
fun App() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DemoScreen()
        }
    }
}

/**
 * Demo screen showcasing StatusBox functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoScreen() {
    // State management
    var uiState by remember { mutableStateOf<UIState>(UIState.Initial) }
    var loading by remember { mutableStateOf(true to null as Any?) }
    var itemCount by remember { mutableIntStateOf(5) }
    val scope = rememberCoroutineScope()

    // Auto-load data on first composition
    LaunchedEffect(Unit) {
        delay(2000)
        uiState = DemoUIState.Success("StatusBox Demo")
        loading = false to null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = { Text("StatusBox Demo - Web & Desktop") }
            )
        },
        bottomBar = {
            ControlPanel(
                onSuccessClick = {
                    uiState = DemoUIState.Success("Success State!")
                    loading = false to null
                },
                onErrorClick = {
                    uiState = UIState.Error("Something went wrong!")
                    loading = false to null
                },
                onEmptyClick = {
                    uiState = UIState.Empty()
                    loading = false to null
                },
                onLoadingClick = {
                    loading = true to null
                },
                onReloadClick = {
                    uiState = UIState.Initial
                    loading = true to null
                    scope.launch {
                        delay(2000)
                        uiState = DemoUIState.Success("Reloaded!")
                        loading = false to null
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState is DemoUIState.Success) {
                CounterButtons(
                    count = itemCount,
                    onIncrement = { itemCount++ },
                    onDecrement = { if (itemCount > 0) itemCount-- }
                )
            }
        }
    ) { paddingValues ->
        StatusBox<DemoUIState.Success>(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            uiState = uiState,
            loadingState = loading,
            loadingComponentBlock = {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) { successState ->
            ContentList(
                title = successState.message,
                itemCount = itemCount
            )
        }
    }
}

/**
 * Control panel with state change buttons
 */
@Composable
fun ControlPanel(
    onSuccessClick: () -> Unit,
    onErrorClick: () -> Unit,
    onEmptyClick: () -> Unit,
    onLoadingClick: () -> Unit,
    onReloadClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .shadow(elevation = 4.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = onSuccessClick) {
            Text("Success")
        }
        Button(onClick = onErrorClick) {
            Text("Error")
        }
        Button(onClick = onEmptyClick) {
            Text("Empty")
        }
        Button(onClick = onLoadingClick) {
            Text("Loading")
        }
        Button(onClick = onReloadClick) {
            Text("Reload")
        }
    }
}

/**
 * Floating action buttons for counter
 */
@Composable
fun CounterButtons(
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FloatingActionButton(
            onClick = onIncrement,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text("+", style = MaterialTheme.typography.headlineMedium)
        }
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        FloatingActionButton(
            onClick = onDecrement,
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text("-", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

/**
 * Content list shown in success state
 */
@Composable
fun ContentList(
    title: String,
    itemCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        for (i in 0..itemCount) {
            ContentItem(index = i)
        }
    }
}

/**
 * Individual content item
 */
@Composable
fun ContentItem(index: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Item #$index",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

/**
 * Custom UI state for demo
 */
sealed class DemoUIState : UIState {
    data class Success(val message: String) : DemoUIState()
}
