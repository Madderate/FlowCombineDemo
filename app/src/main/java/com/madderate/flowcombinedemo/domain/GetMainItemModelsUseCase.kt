package com.madderate.flowcombinedemo.domain

import androidx.paging.PagingData
import com.madderate.flowcombinedemo.data.main.MainItemModel
import com.madderate.flowcombinedemo.data.main.MainRepository
import kotlinx.coroutines.flow.Flow

class GetMainItemModelsUseCase(private val repository: MainRepository) {
    operator fun invoke(): Flow<PagingData<MainItemModel>> {
        return repository.getMainItemPager().flow
    }
}