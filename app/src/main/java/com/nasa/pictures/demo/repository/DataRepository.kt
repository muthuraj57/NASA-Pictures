/* $Id$ */
package com.nasa.pictures.demo.repository

import com.nasa.pictures.demo.di.DispatcherProvider
import com.nasa.pictures.demo.model.Data
import com.nasa.pictures.demo.model.JsonData
import com.nasa.pictures.demo.util.DateConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Muthuraj on 17/03/21.
 */
class DataRepository @Inject constructor(
    private val assetFileProvider: AssetFileProvider,
    private val dispatcherProvider: DispatcherProvider,
    private val dateConverter: DateConverter
) {
    private var dataset: List<Data>? = null

    private suspend fun parseData(): List<Data> {
        return withContext(dispatcherProvider.Default) {
            val dataListType = Types.newParameterizedType(List::class.java, JsonData::class.java)
            val adapter = Moshi.Builder()
                .build()
                .adapter<List<JsonData>>(dataListType)
            val json = assetFileProvider.openFile("data.json").readBytes().decodeToString()
            val datePattern = "yyyy-MM-dd"
            adapter.fromJson(json)!!.map {
                Data(
                    title = it.title,
                    copyright = it.copyright,
                    publishedDate = dateConverter.convertToMilliSec(it.date, datePattern),
                    explanation = it.explanation,
                    hdUrl = it.hdUrl,
                    url = it.url
                )
            }
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