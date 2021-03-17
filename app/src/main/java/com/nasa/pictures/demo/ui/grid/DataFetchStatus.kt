/* $Id$ */
package com.nasa.pictures.demo.ui.grid

import com.nasa.pictures.demo.model.Data

/**
 * Created by Muthuraj on 17/03/21.
 */
sealed class DataFetchStatus {
    object InProgress : DataFetchStatus()
    data class Done(val dataset: List<Data>) : DataFetchStatus()
}