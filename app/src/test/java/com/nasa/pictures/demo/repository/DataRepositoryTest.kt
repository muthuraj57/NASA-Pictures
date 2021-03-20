package com.nasa.pictures.demo.repository

import com.google.common.truth.Truth.assertThat
import com.nasa.pictures.demo.testUtil.TestAssetFileProvider
import com.nasa.pictures.demo.testUtil.TestDispatchers
import com.nasa.pictures.demo.util.DateConverter
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

/**
 * Created by Muthuraj on 17/03/21.
 */
class DataRepositoryTest {

    private lateinit var dataRepository: DataRepository

    @Before
    fun setUp() {
        dataRepository = DataRepository(TestAssetFileProvider(), TestDispatchers(), DateConverter())
    }

    @Test
    fun `getDataSortedByLatest returns valid data`() {
        val data = runBlocking { dataRepository.getDataSortedByLatest() }
        assertThat(data).isNotEmpty()
    }

    @Test
    fun `getDataSortedByLatest returns valid sorted data`() {
        val data = runBlocking { dataRepository.getDataSortedByLatest() }

        //Truth has only isInOrder method which will check if list is in ascending order.
        //So reverse the source before proceeding with the check.
        val dataToCheck = data.reversed()
        assertThat(dataToCheck).isInOrder()
    }
}