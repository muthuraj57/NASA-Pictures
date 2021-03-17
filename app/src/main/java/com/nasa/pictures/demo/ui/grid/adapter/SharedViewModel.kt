/* $Id$ */
package com.nasa.pictures.demo.ui.grid.adapter

import androidx.lifecycle.ViewModel
import com.nasa.pictures.demo.repository.DataRepository
import com.nasa.pictures.demo.ui.grid.DataFetchStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by Muthuraj on 17/03/21.
 */
@HiltViewModel
class SharedViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    fun getDataFlow(): Flow<DataFetchStatus> {
        return flow {
            emit(DataFetchStatus.InProgress)
            emit(DataFetchStatus.Done(dataRepository.getData()))
        }
    }

    suspend fun getData(position: Int) = dataRepository.getData()[position]
}