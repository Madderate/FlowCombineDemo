package com.madderate.flowcombinedemo

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.madderate.flowcombinedemo.data.main.MainApi
import com.madderate.flowcombinedemo.data.main.MainItemModel
import com.madderate.flowcombinedemo.data.main.MainRepository
import com.madderate.flowcombinedemo.domain.GetMainItemModelsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val api = MainApi()
    private val repository = MainRepository(api)

    private val getMainItemModels = GetMainItemModelsUseCase(repository)


    private val _selectedItemsFlow = MutableStateFlow<List<String>>(emptyList())
    private val selectedItemsFlow: StateFlow<List<String>>
        get() = _selectedItemsFlow

    val mainItemModelsFlow: Flow<PagingData<MainItemModel>> = getMainItemModels()
        .cachedIn(viewModelScope)
        .combine(_selectedItemsFlow) { pagingData, selectedItemIds ->
            pagingData.map {
                val shouldBeSelected = selectedItemIds.contains(it.id)
                val isSelected = it.isSelected
                if (shouldBeSelected && !isSelected) {
                    it.copy(isSelected = true)
                } else if (!shouldBeSelected && isSelected) {
                    it.copy(isSelected = false)
                } else {
                    it
                }
            }
        }

    fun itemClicked(model: MainItemModel?) {
        Log.d("MainViewModel", "itemClicked: model=$model")
        val itemId = model?.id ?: return

        val selectedItems = selectedItemsFlow.value.toMutableList()
        if (selectedItems.contains(itemId)) {
            selectedItems.remove(itemId)
            _selectedItemsFlow.value = selectedItems
            return
        }

        selectedItems.add(itemId)
        _selectedItemsFlow.value = selectedItems
    }
}