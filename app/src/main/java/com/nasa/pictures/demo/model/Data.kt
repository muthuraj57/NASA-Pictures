/* $Id$ */
package com.nasa.pictures.demo.model

/**
 * Created by Muthuraj on 20/03/21.
 *
 * Model used in presentation layer. This is converted from [JsonData].
 */
data class Data(
    val title: String,
    val copyright: String?,
    val publishedDate: Long,
    val explanation: String,
    val hdUrl: String,
    val url: String
) : Comparable<Data> {
    /**
     * Used to identify images with same data among grid view, detail view and indicator view.
     * */
    val transitionName = title + url

    override fun compareTo(other: Data): Int {
        return publishedDate.compareTo(other.publishedDate)
    }
}