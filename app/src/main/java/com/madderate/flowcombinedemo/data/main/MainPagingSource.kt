package com.madderate.flowcombinedemo.data.main

import androidx.paging.PagingSource
import androidx.paging.PagingState

class MainPagingSource(private val api: MainApi) : PagingSource<Int, MainItemModel>() {
    override fun getRefreshKey(state: PagingState<Int, MainItemModel>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MainItemModel> {
        return when (params) {
            is LoadParams.Refresh -> getLoadResult()
            is LoadParams.Append -> getLoadResult(params.key)
            else -> throw IllegalArgumentException("")
        }
    }

    private suspend fun getLoadResult(start: Int? = null): LoadResult<Int, MainItemModel> = try {
        val page = api.getMainPage(start ?: 0)
        val next = if (page.hasMore) {
            page.next
        } else {
            null
        }
        LoadResult.Page(page.items, start, next)
    } catch (e: Exception) {
        LoadResult.Error(e)
    }
}