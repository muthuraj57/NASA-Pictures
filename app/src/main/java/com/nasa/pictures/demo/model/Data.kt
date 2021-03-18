/* $Id$ */
package com.nasa.pictures.demo.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by Muthuraj on 17/03/21.
 */
@JsonClass(generateAdapter = true)
data class Data(
    val title: String,
    val copyright: String?,
    val date: String,
    val explanation: String,
    @field:Json(name = "hdurl")
    val hdUrl: String,
    val url: String
) {

    /**
     * Used to identify images with same data among grid view, detail view and indicator view.
     * */
    val transitionName = title + url
}