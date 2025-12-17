package com.ocnyang.status_box

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

@Composable
fun <T : Any> LazyListScope.itemsLoadStateWrap(pagingItems: LazyPagingItems<T>) {
    when (pagingItems.loadState.refresh) {
        is LoadState.Loading -> {
            // 初始加载
            item(key = "paging_loading") {
                PagingLoadingItem()
            }
        }

        is LoadState.Error -> {
            // 初始加载错误
            item(key = "paging_error") {
                PagingErrorItem(onClick = { pagingItems.refresh() })
            }
        }

        is LoadState.NotLoading -> {
            if (pagingItems.itemCount == 0) {
                // 空数据
                item(key = "paging_empty") {
                    PagingEmptyItem()
                }
            } else {
                when (pagingItems.loadState.append) {
                    is LoadState.Loading -> {
                        // 底部加载更多
                        item(key = "paging_load_more") {
                            PagingLoadMoreItem()
                        }
                    }

                    is LoadState.Error -> {
                        // 底部加载更多错误
                        item(key = "paging_load_more_error") {
                            PagingLoadMoreErrorItem(onClick = { pagingItems.retry() })
                        }
                    }

                    is LoadState.NotLoading -> {
                        if (pagingItems.loadState.append.endOfPaginationReached) {
                            // 已经没有更多了
                            item(key = "paging_no_more") {
                                PagingNoMoreItem()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun <T : Any> LazyGridScope.itemsLoadStateWrap(pagingItems: LazyPagingItems<T>) {
    when (pagingItems.loadState.refresh) {
        is LoadState.Loading -> {
            // 初始加载
            item(key = "paging_loading", span = { GridItemSpan(maxLineSpan) }) {
                PagingLoadingItem()
            }
        }

        is LoadState.Error -> {
            // 初始加载错误
            item(key = "paging_error", span = { GridItemSpan(maxLineSpan) }) {
                PagingErrorItem(onClick = { pagingItems.refresh() })
            }
        }

        is LoadState.NotLoading -> {
            if (pagingItems.itemCount == 0) {
                // 空数据
                item(key = "paging_empty", span = { GridItemSpan(maxLineSpan) }) {
                    PagingEmptyItem()
                }
            } else {
                when (pagingItems.loadState.append) {
                    is LoadState.Loading -> {
                        // 底部加载更多
                        item(key = "paging_load_more", span = { GridItemSpan(maxLineSpan) }) {
                            PagingLoadMoreItem()
                        }
                    }

                    is LoadState.Error -> {
                        // 底部加载更多错误
                        item(key = "paging_load_more_error", span = { GridItemSpan(maxLineSpan) }) {
                            PagingLoadMoreErrorItem(onClick = { pagingItems.retry() })
                        }
                    }

                    is LoadState.NotLoading -> {
                        if (pagingItems.loadState.append.endOfPaginationReached) {
                            // 已经没有更多了
                            item(key = "paging_no_more", span = { GridItemSpan(maxLineSpan) }) {
                                PagingNoMoreItem()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PagingLoadingItem() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        DefaultLoadingStateView()
    }
}

@Composable
fun PagingErrorItem(onClick: () -> Unit = {}, hintText: String = "", iconRes: Int = 0) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(Modifier.wrapContentSize()) {
            DefaultErrorStateView()
        }
    }
}

@Composable
fun PagingEmptyItem() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(Modifier.wrapContentSize()) {
            DefaultEmptyStateView()
        }
    }
}

@Composable
fun PagingLoadMoreItem() {
    Box(
        modifier = Modifier.wrapContentSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DefaultLoadingStateView()
        }
    }
}

@Composable
fun PagingLoadMoreErrorItem(onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("加载更多失败  ", fontSize = 10.sp)
            Text("{点击重试}", fontSize = 10.sp)
        }
    }
}

@Composable
fun PagingNoMoreItem() {
    Box(
        modifier = Modifier.wrapContentSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("- 已经到底了 -", fontSize = 10.sp)
        }
    }
}
