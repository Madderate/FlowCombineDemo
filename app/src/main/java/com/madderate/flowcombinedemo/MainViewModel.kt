package com.madderate.flowcombinedemo

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _mutex = Mutex()

    private val _baseList = MutableStateFlow<List<String>>(emptyList())
    private val _listAddedCache = MutableStateFlow<List<String>>(emptyList())
    private val _listRemovedCache = MutableStateFlow<List<String>>(emptyList())

    val list: Flow<List<String>>
        get() = combine(_baseList, _listAddedCache, _listRemovedCache) { base, added, removed ->
            val result = mutableListOf<String>()
            for (item in base) {
                if (removed.contains(item)) {
                    continue
                }
                result.add(item)
            }
            result.addAll(added)
            result
        }


    init {
        _baseList.value = listOf(
            "你好",
            "谢谢",
            "小笼包",
            "再见",
        )
    }


    fun add(value: String) {
        if (_baseList.value.contains(value) && !_listRemovedCache.value.contains(value)) {
            Log.w(TAG, "addItem: already in baseList and not be removed. value=$value")
            return
        }

        if (_listAddedCache.value.contains(value)) {
            Log.w(TAG, "addItem: already in addedCache. value=$value")
            return
        }

        _listAddedCache.update {
            it.toMutableList().apply {
                add(value)
            }
        }
        Log.d(TAG, "addItem: add value! value=$value")
    }

    fun remove(value: String) {
        if (_baseList.value.contains(value)) {
            _listRemovedCache.update {
                it.toMutableList().apply {
                    add(value)
                }
            }
            Log.d(TAG, "remove: add value into removedCache. value=$value")
            return
        }

        if (!_listAddedCache.value.contains(value)) {
            Log.w(TAG, "remove: not in baseList and addedCache... value=$value")
            return
        }
        _listAddedCache.update {
            it.toMutableList().apply {
                remove(value)
            }
        }
        Log.d(TAG, "remove: remove value from addedCache. value=$value")
    }

    fun sync() = viewModelScope.launch(Dispatchers.IO) {
        _mutex.lock(TAG)
        val result = mutableListOf<String>()
        val baseItems = _baseList.value
        val removedItems = _listRemovedCache.value
        val addedItems = _listAddedCache.value
        for (item in baseItems) {
            if (removedItems.contains(item)) {
                continue
            }
            result.add(item)
        }
        result.addAll(addedItems)
        _listRemovedCache.value = emptyList()
        _listAddedCache.value = emptyList()
        _baseList.value = result
        _mutex.unlock(TAG)
    }


    companion object {
        const val TAG = "MainViewModel"
    }
}