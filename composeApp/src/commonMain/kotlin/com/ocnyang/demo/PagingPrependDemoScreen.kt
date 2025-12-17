package com.ocnyang.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import com.ocnyang.status_box.items

/**
 * Paging Prepend Demo - Load previous at top (like chat messages)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagingPrependDemoScreen(onNavigateBack: () -> Unit) {
    val pager = remember {
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            )
        ) {
            MockReversePagingSource()
        }
    }

    val lazyPagingItems = pager.flow.collectAsLazyPagingItems()
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on first load (like chat)
    LaunchedEffect(lazyPagingItems.itemCount) {
        if (lazyPagingItems.itemCount > 0 && listState.firstVisibleItemIndex == 0) {
            listState.scrollToItem(lazyPagingItems.itemCount - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = { Text("Paging - Load Previous (Prepend)") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            reverseLayout = true  // Messages at bottom, scroll up to load more
        ) {
            // Use items() extension from LazyPagingItemStatusBox
            items(
                pagingItems = lazyPagingItems,
                key = { it.id }
            ) { item ->
                ChatMessageCard(item = item)
            }
        }
    }
}

@Composable
fun ChatMessageCard(item: MockItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
