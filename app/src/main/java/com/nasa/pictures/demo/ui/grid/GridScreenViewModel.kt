/* $Id$ */
package com.nasa.pictures.demo.ui.grid

import androidx.lifecycle.ViewModel
import com.nasa.pictures.demo.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Muthuraj on 17/03/21.
 */
@HiltViewModel
class GridScreenViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {
    suspend fun getData() = dataRepository.getData()
}