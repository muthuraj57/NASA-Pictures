/* $Id$ */
package com.nasa.pictures.demo.room.joinData

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

/**
 * Created by Muthuraj on 16/04/21.
 *
 * Jambav, Zoho Corporation
 */
@Entity(tableName = "JsonData")
data class JsonDataEntity(
    @PrimaryKey
    val date: String,
    val title: String,
    val copyright: String?,
    val explanation: String,
    @field:Json(name = "hdurl")
    val hdUrl: String,
    val url: String
)