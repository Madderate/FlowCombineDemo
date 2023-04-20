package com.madderate.flowcombinedemo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.madderate.flowcombinedemo.data.main.MainApi
import com.madderate.flowcombinedemo.data.main.MainItemModel
import com.madderate.flowcombinedemo.data.main.MainRepository
import com.madderate.flowcombinedemo.domain.GetMainItemModelsUseCase
import kotlinx.coroutines.flow.Flow

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val api = MainApi()
    private val repository = MainRepository(api)

    private val getMainItemModels = GetMainItemModelsUseCase(repository)


    val mainItemModelsFlow: Flow<PagingData<MainItemModel>> = getMainItemModels()
        .cachedIn(viewModelScope)
}