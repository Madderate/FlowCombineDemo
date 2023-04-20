package com.madderate.flowcombinedemo.data.base

data class PageModel<T> constructor(
    val hasMore: Boolean,
    val items: List<T>,
    val next: Int? = null,
)