/* $Id$ */
package com.nasa.pictures.demo.repository

import com.nasa.pictures.demo.di.DispatcherProvider
import com.nasa.pictures.demo.model.Data
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Muthuraj on 17/03/21.
 */
class DataRepository @Inject constructor(
    private val assetFileProvider: AssetFileProvider,
    private val dispatcherProvider: DispatcherProvider
) {
    private var dataset: List<Data>? = null

    private suspend fun parseData(): List<Data> {
        return withContext(dispatcherProvider.Default) {
            val dataListType = Types.newParameterizedType(List::class.java, Data::class.java)
            val adapter = Moshi.Builder()
                .build()
                .adapter<List<Data>>(dataListType)
            val json = assetFileProvider.openFile("data.json").readBytes().decodeToString()
            adapter.fromJson(json)!!
        }
    }

    suspend fun getData(): List<Data> {
        if (dataset != null) {
            return dataset!!
        }
        dataset = parseData()
        return dataset!!
    }
}