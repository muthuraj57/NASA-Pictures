package com.nasa.pictures.demo.ui.grid

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.nasa.pictures.demo.repository.DataRepository
import com.nasa.pictures.demo.testUtil.TestAssetFileProvider
import com.nasa.pictures.demo.testUtil.TestDispatchers
import com.nasa.pictures.demo.util.DateConverter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by Muthuraj on 20/03/21.
 */
class MainScreenViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataRepository: DataRepository
    private lateinit var viewModel: MainScreenViewModel

    @Before
    fun setUp() {
        dataRepository = DataRepository(TestAssetFileProvider(), TestDispatchers(), DateConverter())
        viewModel = MainScreenViewModel(SavedStateHandle(), dataRepository)
    }


    @Test
    fun `getDataFlow emits progress state first`() {
        val firstItem = runBlocking { viewModel.getDataFlow().first() }
        assertThat(firstItem).isEqualTo(DataFetchStatus.InProgress)
    }

    @Test
    fun `getDataFlow emits done state at last`() {
        val lastItem = runBlocking { viewModel.getDataFlow().toList().last() }
        assertThat(lastItem).isInstanceOf(DataFetchStatus.Done::class.java)
    }

    @Test
    fun `getDataFlow emits done state with valid data at last`() {
        val lastItem =
            runBlocking { viewModel.getDataFlow().toList().last() } as DataFetchStatus.Done
        assertThat(lastItem.dataset).isNotEmpty()
    }

    @Test
    fun `selectedDetailItemFlow has null as initial value`() {
        val firstItem = runBlocking { viewModel.selectedDetailItemFlow.first() }
        assertThat(firstItem).isNull()
    }

    @Test
    fun `when onGritItemClicked is called with a position, selectedDetailItemFlow emits the position and related data`() {
        val allData = runBlocking { dataRepository.getDataSortedByLatest() }
        viewModel.onGridItemClicked(5)
        val secondItem = runBlocking { viewModel.selectedDetailItemFlow.first() }
        assertThat(secondItem).isEqualTo(5 to allData[5])
    }

    @Test
    fun `when onDetailItemClosed is called, selectedDetailItemFlow emits null`() {
        viewModel.onGridItemClicked(5)
        viewModel.onDetailItemClosed()
        val secondItem = runBlocking { viewModel.selectedDetailItemFlow.first() }
        assertThat(secondItem).isNull()
    }
}