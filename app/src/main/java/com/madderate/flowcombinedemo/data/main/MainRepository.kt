package com.madderate.flowcombinedemo.data.main

import androidx.paging.Pager
import androidx.paging.PagingConfig

class MainRepository(private val api: MainApi) {
    private val config = PagingConfig(
        pageSize = 1,
        prefetchDistance = 1,
        enablePlaceholders = false,
        initialLoadSize = 1,
    )

    fun getMainItemPager(): Pager<Int, MainItemModel> {
        return Pager(config = config) {
            MainPagingSource(api)
        }
    }
}