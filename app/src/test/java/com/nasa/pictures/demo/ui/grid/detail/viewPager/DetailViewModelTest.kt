package com.nasa.pictures.demo.ui.grid.detail.viewPager

import com.google.common.truth.Truth.assertThat
import com.nasa.pictures.demo.model.Data
import com.nasa.pictures.demo.util.DateConverter
import org.junit.Test

/**
 * Created by Muthuraj on 20/03/21.
 */
class DetailViewModelTest {

    @Test
    fun `publishedData returns valid formatted time for given input`() {
        val dummyData = Data(
            "Dummy title",
            explanation = "Dummy explanation, ",
            hdUrl = "https://www.google.com",
            url = "https://www.google.com",
            copyright = null,
            publishedDate = 1616198400000
        )
        val formattedDate = DetailViewModel(dummyData, DateConverter()).publishedDate
        assertThat(formattedDate).isEqualTo("Mar 20, 2021")
    }
}