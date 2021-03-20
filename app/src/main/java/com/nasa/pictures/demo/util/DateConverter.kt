/* $Id$ */
package com.nasa.pictures.demo.util

import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Muthuraj on 20/03/21.
 */
@Singleton
class DateConverter @Inject constructor() {

    private val utcTimeZone by lazy { TimeZone.getTimeZone("UTC") }

    fun convertToMilliSec(date: String, pattern: String): Long {
        val format = SimpleDateFormat(pattern).apply { timeZone = utcTimeZone }
        return format.parse(date).time
    }

    fun formatTime(time: Long, pattern: String): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).apply { timeZone = utcTimeZone }
            .format(Date(time))
    }
}