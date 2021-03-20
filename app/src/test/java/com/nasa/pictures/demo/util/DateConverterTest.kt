package com.nasa.pictures.demo.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.lang.IllegalArgumentException

/**
 * Created by Muthuraj on 20/03/21.
 */
class DateConverterTest {

    @Test
    fun `when valid params are passed, then convertToMilliSec returns valid result`() {
        val result = DateConverter().convertToMilliSec("2021-03-20", "yyy-MM-dd")
        assertThat(result).isEqualTo(1616198400000)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when invalid pattern is passed, then convertToMilliSec throws error`() {
        DateConverter().convertToMilliSec("2021-03-20", "yyf-MM-dd")
    }

    @Test
    fun `when valid params are passed, then formatTime returns valid formatted date`() {
        val result = DateConverter().formatTime(1616198400000, "MMM dd, yyyy")
        assertThat(result).isEqualTo("Mar 20, 2021")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when invalid pattern is passed, then formatTime throws error`() {
        DateConverter().formatTime(1616198400000, "MMM dd, yyyf")
    }
}