package com.nasa.pictures.demo.repository

import com.google.common.truth.Truth.assertThat
import com.nasa.pictures.demo.testUtil.TestAssetFileProvider
import com.nasa.pictures.demo.testUtil.TestDispatchers
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
        dataRepository = DataRepository(TestAssetFileProvider(), TestDispatchers())
    }

    @Test
    fun `getData returns valid data`() {
        val data = runBlocking { dataRepository.getData() }
        assertThat(data).isNotEmpty()
    }
}