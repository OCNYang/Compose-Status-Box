package com.ocnyang.demo

import androidx.paging.*
import kotlinx.coroutines.delay

/**
 * Mock data source for Paging3
 */
data class MockItem(
    val id: Int,
    val title: String,
    val description: String
)

/**
 * Paging Source for bottom loading (Append)
 */
class MockPagingSource : PagingSource<Int, MockItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MockItem> {
        return try {
            val page = params.key ?: 0
            // Simulate network delay
            delay(1500)

            // Simulate loading 20 items per page
            val pageSize = 20
            val items = List(pageSize) { index ->
                val itemId = page * pageSize + index
                MockItem(
                    id = itemId,
                    title = "Item #$itemId",
                    description = "Description for item $itemId"
                )
            }

            LoadResult.Page(
                data = items,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (page < 10) page + 1 else null // Max 10 pages
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MockItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

/**
 * Paging Source for top loading (Prepend)
 */
class MockReversePagingSource : PagingSource<Int, MockItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MockItem> {
        return try {
            val page = params.key ?: 0
            // Simulate network delay
            delay(1500)

            // Simulate loading 20 items per page (reverse order)
            val pageSize = 20
            val items = List(pageSize) { index ->
                val itemId = page * pageSize + index
                MockItem(
                    id = itemId,
                    title = "Message #$itemId",
                    description = "Content of message $itemId"
                )
            }.reversed()

            LoadResult.Page(
                data = items,
                prevKey = if (page < 10) page + 1 else null, // Max 10 pages
                nextKey = if (page == 0) null else page - 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MockItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.minus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.plus(1)
        }
    }
}
