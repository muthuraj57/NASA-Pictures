/* $Id$ */
package com.nasa.pictures.demo.ui.grid

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.nasa.pictures.demo.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by Muthuraj on 17/03/21.
 */
@HiltViewModel
class MainScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    private val selectedDetailItemPosition =
        savedStateHandle.getLiveData(SELECTED_DETAIL_ITEM_POSITION, -1)
    val selectedDetailItemFlow = selectedDetailItemPosition.asFlow()
        .map { selectedPosition ->
            when {
                selectedPosition != -1 -> selectedPosition to dataRepository.getDataSortedByLatest()[selectedPosition]
                else -> null
            }
        }

    fun getDataFlow(): Flow<DataFetchStatus> {
        return flow {
            emit(DataFetchStatus.InProgress)
            emit(DataFetchStatus.Done(dataRepository.getDataSortedByLatest()))
        }
    }

    fun onGridItemClicked(position: Int) {
        selectedDetailItemPosition.value = position
    }

    fun onDetailItemClosed() {
        selectedDetailItemPosition.value = -1
    }

    companion object {
        private const val SELECTED_DETAIL_ITEM_POSITION = "selected_detail_item_position"
    }
}