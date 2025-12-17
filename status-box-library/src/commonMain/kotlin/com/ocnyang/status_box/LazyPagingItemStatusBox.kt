package com.ocnyang.status_box

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.ocnyang.status_box.resources.Res
import com.ocnyang.status_box.resources.paging_click_retry
import com.ocnyang.status_box.resources.paging_load_more_error
import com.ocnyang.status_box.resources.paging_no_more
import org.jetbrains.compose.resources.stringResource

// ================================================================================================
// LazyColumn / LazyRow Support
// ================================================================================================

/**
 * Complete items() extension for LazyList (LazyColumn/LazyRow) with automatic Paging state handling.
 *
 * This function renders both the data items and all loading states (initial loading, errors, empty state,
 * load more, etc.) automatically.
 *
 * @param pagingItems The LazyPagingItems from Paging3
 * @param key Optional stable key provider for items
 * @param contentType Optional content type provider for items
 * @param loadingContent Custom initial loading component
 * @param errorContent Custom initial error component (receives error and retry callback)
 * @param emptyContent Custom empty state component
 * @param appendLoadingContent Custom "loading more" component (bottom)
 * @param appendErrorContent Custom "load more error" component (receives error and retry callback)
 * @param prependLoadingContent Custom "loading more" component (top)
 * @param prependErrorContent Custom "prepend error" component (receives error and retry callback)
 * @param noMoreContent Custom "no more data" component
 * @param itemContent Content composable for each data item
 */
fun <T : Any> LazyListScope.items(
    pagingItems: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    contentType: ((item: T) -> Any)? = null,
    loadingContent: @Composable LazyItemScope.() -> Unit = { PagingLoadingItem() },
    errorContent: @Composable LazyItemScope.(error: Throwable, retry: () -> Unit) -> Unit = { error, retry ->
        PagingErrorItem(error = error, onRetry = retry)
    },
    emptyContent: @Composable LazyItemScope.() -> Unit = { PagingEmptyItem() },
    appendLoadingContent: @Composable LazyItemScope.() -> Unit = { PagingLoadMoreItem() },
    appendErrorContent: @Composable LazyItemScope.(error: Throwable, retry: () -> Unit) -> Unit = { error, retry ->
        PagingLoadMoreErrorItem(error = error, onRetry = retry)
    },
    prependLoadingContent: @Composable LazyItemScope.() -> Unit = { PagingLoadMoreItem() },
    prependErrorContent: @Composable LazyItemScope.(error: Throwable, retry: () -> Unit) -> Unit = { error, retry ->
        PagingLoadMoreErrorItem(error = error, onRetry = retry)
    },
    noMoreContent: @Composable LazyItemScope.() -> Unit = { PagingNoMoreItem() },
    itemContent: @Composable LazyItemScope.(value: T) -> Unit
) {
    // Handle prepend (top) loading states
    handlePrependState(
        pagingItems = pagingItems,
        loadingContent = prependLoadingContent,
        errorContent = prependErrorContent
    )

    // Handle refresh (initial) loading states
    when (val refresh = pagingItems.loadState.refresh) {
        is LoadState.Loading -> {
            item(key = "paging_refresh_loading") {
                loadingContent()
            }
        }
        is LoadState.Error -> {
            item(key = "paging_refresh_error") {
                errorContent(refresh.error) { pagingItems.refresh() }
            }
        }
        is LoadState.NotLoading -> {
            // Check if data is empty
            if (pagingItems.itemCount == 0) {
                item(key = "paging_empty") {
                    emptyContent()
                }
            } else {
                // Render actual data items
                items(
                    count = pagingItems.itemCount,
                    key = pagingItems.itemKey(key),
                    contentType = pagingItems.itemContentType(contentType)
                ) { index ->
                    val item = pagingItems[index]
                    if (item != null) {
                        itemContent(item)
                    } else {
                        // Placeholder (if placeholders are enabled)
                        PagingPlaceholderItem()
                    }
                }
            }
        }
    }

    // Handle append (bottom) loading states
    handleAppendState(
        pagingItems = pagingItems,
        loadingContent = appendLoadingContent,
        errorContent = appendErrorContent,
        noMoreContent = noMoreContent
    )
}

/**
 * Handle prepend (top) loading state for LazyList
 */
private fun <T : Any> LazyListScope.handlePrependState(
    pagingItems: LazyPagingItems<T>,
    loadingContent: @Composable LazyItemScope.() -> Unit,
    errorContent: @Composable LazyItemScope.(error: Throwable, retry: () -> Unit) -> Unit
) {
    when (val prepend = pagingItems.loadState.prepend) {
        is LoadState.Loading -> {
            item(key = "paging_prepend_loading") {
                loadingContent()
            }
        }
        is LoadState.Error -> {
            item(key = "paging_prepend_error") {
                errorContent(prepend.error) { pagingItems.retry() }
            }
        }
        is LoadState.NotLoading -> {
            // No prepend UI needed
        }
    }
}

/**
 * Handle append (bottom) loading state for LazyList
 */
private fun <T : Any> LazyListScope.handleAppendState(
    pagingItems: LazyPagingItems<T>,
    loadingContent: @Composable LazyItemScope.() -> Unit,
    errorContent: @Composable LazyItemScope.(error: Throwable, retry: () -> Unit) -> Unit,
    noMoreContent: @Composable LazyItemScope.() -> Unit
) {
    when (val append = pagingItems.loadState.append) {
        is LoadState.Loading -> {
            item(key = "paging_append_loading") {
                loadingContent()
            }
        }
        is LoadState.Error -> {
            item(key = "paging_append_error") {
                errorContent(append.error) { pagingItems.retry() }
            }
        }
        is LoadState.NotLoading -> {
            if (append.endOfPaginationReached && pagingItems.itemCount > 0) {
                item(key = "paging_no_more") {
                    noMoreContent()
                }
            }
        }
    }
}

// ================================================================================================
// LazyVerticalGrid / LazyHorizontalGrid Support
// ================================================================================================

/**
 * Complete items() extension for LazyGrid with automatic Paging state handling.
 */
fun <T : Any> LazyGridScope.items(
    pagingItems: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    span: ((item: T) -> GridItemSpan)? = null,
    contentType: ((item: T) -> Any)? = null,
    loadingContent: @Composable LazyGridItemScope.() -> Unit = { PagingLoadingItem() },
    errorContent: @Composable LazyGridItemScope.(error: Throwable, retry: () -> Unit) -> Unit = { error, retry ->
        PagingErrorItem(error = error, onRetry = retry)
    },
    emptyContent: @Composable LazyGridItemScope.() -> Unit = { PagingEmptyItem() },
    appendLoadingContent: @Composable LazyGridItemScope.() -> Unit = { PagingLoadMoreItem() },
    appendErrorContent: @Composable LazyGridItemScope.(error: Throwable, retry: () -> Unit) -> Unit = { error, retry ->
        PagingLoadMoreErrorItem(error = error, onRetry = retry)
    },
    prependLoadingContent: @Composable LazyGridItemScope.() -> Unit = { PagingLoadMoreItem() },
    prependErrorContent: @Composable LazyGridItemScope.(error: Throwable, retry: () -> Unit) -> Unit = { error, retry ->
        PagingLoadMoreErrorItem(error = error, onRetry = retry)
    },
    noMoreContent: @Composable LazyGridItemScope.() -> Unit = { PagingNoMoreItem() },
    itemContent: @Composable LazyGridItemScope.(value: T) -> Unit
) {
    // Handle prepend (top) loading states
    handlePrependStateGrid(
        pagingItems = pagingItems,
        loadingContent = prependLoadingContent,
        errorContent = prependErrorContent
    )

    // Handle refresh (initial) loading states
    when (val refresh = pagingItems.loadState.refresh) {
        is LoadState.Loading -> {
            item(key = "paging_refresh_loading", span = { GridItemSpan(maxLineSpan) }) {
                loadingContent()
            }
        }
        is LoadState.Error -> {
            item(key = "paging_refresh_error", span = { GridItemSpan(maxLineSpan) }) {
                errorContent(refresh.error) { pagingItems.refresh() }
            }
        }
        is LoadState.NotLoading -> {
            if (pagingItems.itemCount == 0) {
                item(key = "paging_empty", span = { GridItemSpan(maxLineSpan) }) {
                    emptyContent()
                }
            } else {
                items(
                    count = pagingItems.itemCount,
                    key = pagingItems.itemKey(key),
                    span = { index ->
                        val item = pagingItems.peek(index)
                        if (item != null && span != null) {
                            span(item)
                        } else {
                            GridItemSpan(1)
                        }
                    },
                    contentType = pagingItems.itemContentType(contentType)
                ) { index ->
                    val item = pagingItems[index]
                    if (item != null) {
                        itemContent(item)
                    } else {
                        PagingPlaceholderItem()
                    }
                }
            }
        }
    }

    // Handle append (bottom) loading states
    handleAppendStateGrid(
        pagingItems = pagingItems,
        loadingContent = appendLoadingContent,
        errorContent = appendErrorContent,
        noMoreContent = noMoreContent
    )
}

private fun <T : Any> LazyGridScope.handlePrependStateGrid(
    pagingItems: LazyPagingItems<T>,
    loadingContent: @Composable LazyGridItemScope.() -> Unit,
    errorContent: @Composable LazyGridItemScope.(error: Throwable, retry: () -> Unit) -> Unit
) {
    when (val prepend = pagingItems.loadState.prepend) {
        is LoadState.Loading -> {
            item(key = "paging_prepend_loading", span = { GridItemSpan(maxLineSpan) }) {
                loadingContent()
            }
        }
        is LoadState.Error -> {
            item(key = "paging_prepend_error", span = { GridItemSpan(maxLineSpan) }) {
                errorContent(prepend.error) { pagingItems.retry() }
            }
        }
        is LoadState.NotLoading -> {}
    }
}

private fun <T : Any> LazyGridScope.handleAppendStateGrid(
    pagingItems: LazyPagingItems<T>,
    loadingContent: @Composable LazyGridItemScope.() -> Unit,
    errorContent: @Composable LazyGridItemScope.(error: Throwable, retry: () -> Unit) -> Unit,
    noMoreContent: @Composable LazyGridItemScope.() -> Unit
) {
    when (val append = pagingItems.loadState.append) {
        is LoadState.Loading -> {
            item(key = "paging_append_loading", span = { GridItemSpan(maxLineSpan) }) {
                loadingContent()
            }
        }
        is LoadState.Error -> {
            item(key = "paging_append_error", span = { GridItemSpan(maxLineSpan) }) {
                errorContent(append.error) { pagingItems.retry() }
            }
        }
        is LoadState.NotLoading -> {
            if (append.endOfPaginationReached && pagingItems.itemCount > 0) {
                item(key = "paging_no_more", span = { GridItemSpan(maxLineSpan) }) {
                    noMoreContent()
                }
            }
        }
    }
}

// ================================================================================================
// LazyStaggeredGrid Support
// ================================================================================================

/**
 * Complete items() extension for LazyStaggeredGrid with automatic Paging state handling.
 */
fun <T : Any> LazyStaggeredGridScope.items(
    pagingItems: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    span: ((item: T) -> StaggeredGridItemSpan)? = null,
    contentType: ((item: T) -> Any)? = null,
    loadingContent: @Composable LazyStaggeredGridItemScope.() -> Unit = { PagingLoadingItem() },
    errorContent: @Composable LazyStaggeredGridItemScope.(error: Throwable, retry: () -> Unit) -> Unit = { error, retry ->
        PagingErrorItem(error = error, onRetry = retry)
    },
    emptyContent: @Composable LazyStaggeredGridItemScope.() -> Unit = { PagingEmptyItem() },
    appendLoadingContent: @Composable LazyStaggeredGridItemScope.() -> Unit = { PagingLoadMoreItem() },
    appendErrorContent: @Composable LazyStaggeredGridItemScope.(error: Throwable, retry: () -> Unit) -> Unit = { error, retry ->
        PagingLoadMoreErrorItem(error = error, onRetry = retry)
    },
    prependLoadingContent: @Composable LazyStaggeredGridItemScope.() -> Unit = { PagingLoadMoreItem() },
    prependErrorContent: @Composable LazyStaggeredGridItemScope.(error: Throwable, retry: () -> Unit) -> Unit = { error, retry ->
        PagingLoadMoreErrorItem(error = error, onRetry = retry)
    },
    noMoreContent: @Composable LazyStaggeredGridItemScope.() -> Unit = { PagingNoMoreItem() },
    itemContent: @Composable LazyStaggeredGridItemScope.(value: T) -> Unit
) {
    // Handle prepend
    handlePrependStateStaggeredGrid(
        pagingItems = pagingItems,
        loadingContent = prependLoadingContent,
        errorContent = prependErrorContent
    )

    // Handle refresh
    when (val refresh = pagingItems.loadState.refresh) {
        is LoadState.Loading -> {
            item(key = "paging_refresh_loading", span = StaggeredGridItemSpan.FullLine) {
                loadingContent()
            }
        }
        is LoadState.Error -> {
            item(key = "paging_refresh_error", span = StaggeredGridItemSpan.FullLine) {
                errorContent(refresh.error) { pagingItems.refresh() }
            }
        }
        is LoadState.NotLoading -> {
            if (pagingItems.itemCount == 0) {
                item(key = "paging_empty", span = StaggeredGridItemSpan.FullLine) {
                    emptyContent()
                }
            } else {
                items(
                    count = pagingItems.itemCount,
                    key = pagingItems.itemKey(key),
                    span = { index ->
                        val item = pagingItems.peek(index)
                        if (item != null && span != null) {
                            span(item)
                        } else {
                            StaggeredGridItemSpan.SingleLane
                        }
                    },
                    contentType = pagingItems.itemContentType(contentType)
                ) { index ->
                    val item = pagingItems[index]
                    if (item != null) {
                        itemContent(item)
                    } else {
                        PagingPlaceholderItem()
                    }
                }
            }
        }
    }

    // Handle append
    handleAppendStateStaggeredGrid(
        pagingItems = pagingItems,
        loadingContent = appendLoadingContent,
        errorContent = appendErrorContent,
        noMoreContent = noMoreContent
    )
}

private fun <T : Any> LazyStaggeredGridScope.handlePrependStateStaggeredGrid(
    pagingItems: LazyPagingItems<T>,
    loadingContent: @Composable LazyStaggeredGridItemScope.() -> Unit,
    errorContent: @Composable LazyStaggeredGridItemScope.(error: Throwable, retry: () -> Unit) -> Unit
) {
    when (val prepend = pagingItems.loadState.prepend) {
        is LoadState.Loading -> {
            item(key = "paging_prepend_loading", span = StaggeredGridItemSpan.FullLine) {
                loadingContent()
            }
        }
        is LoadState.Error -> {
            item(key = "paging_prepend_error", span = StaggeredGridItemSpan.FullLine) {
                errorContent(prepend.error) { pagingItems.retry() }
            }
        }
        is LoadState.NotLoading -> {}
    }
}

private fun <T : Any> LazyStaggeredGridScope.handleAppendStateStaggeredGrid(
    pagingItems: LazyPagingItems<T>,
    loadingContent: @Composable LazyStaggeredGridItemScope.() -> Unit,
    errorContent: @Composable LazyStaggeredGridItemScope.(error: Throwable, retry: () -> Unit) -> Unit,
    noMoreContent: @Composable LazyStaggeredGridItemScope.() -> Unit
) {
    when (val append = pagingItems.loadState.append) {
        is LoadState.Loading -> {
            item(key = "paging_append_loading", span = StaggeredGridItemSpan.FullLine) {
                loadingContent()
            }
        }
        is LoadState.Error -> {
            item(key = "paging_append_error", span = StaggeredGridItemSpan.FullLine) {
                errorContent(append.error) { pagingItems.retry() }
            }
        }
        is LoadState.NotLoading -> {
            if (append.endOfPaginationReached && pagingItems.itemCount > 0) {
                item(key = "paging_no_more", span = StaggeredGridItemSpan.FullLine) {
                    noMoreContent()
                }
            }
        }
    }
}

// ================================================================================================
// Default Paging Item Components
// ================================================================================================

/**
 * Default loading indicator for initial/refresh loading state
 */
@Composable
fun PagingLoadingItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        DefaultLoadingStateView()
    }
}

/**
 * Default error view for initial/refresh error state
 */
@Composable
fun PagingErrorItem(
    error: Throwable,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onRetry),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DefaultErrorStateView(hintText = error.message)
        }
    }
}

/**
 * Default empty state view
 */
@Composable
fun PagingEmptyItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        DefaultEmptyStateView()
    }
}

/**
 * Default "loading more" indicator (for append/prepend)
 */
@Composable
fun PagingLoadMoreItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        DefaultLoadingStateView()
    }
}

/**
 * Default "load more error" view (for append/prepend error)
 */
@Composable
fun PagingLoadMoreErrorItem(
    error: Throwable,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onRetry),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(Res.string.paging_load_more_error),
                fontSize = 12.sp
            )
            Text(
                text = "  ${stringResource(Res.string.paging_click_retry)}",
                fontSize = 12.sp
            )
        }
    }
}

/**
 * Default "no more data" view
 */
@Composable
fun PagingNoMoreItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.paging_no_more),
            fontSize = 12.sp
        )
    }
}

/**
 * Default placeholder item (shown when placeholders are enabled)
 */
@Composable
fun PagingPlaceholderItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .wrapContentSize()
    ) {
        // Can be customized to show shimmer effect or skeleton screen
        Text(text = "...", fontSize = 12.sp)
    }
}
