/* $Id$ */
package com.nasa.pictures.demo.ui.grid.detail.viewPager

import com.nasa.pictures.demo.model.Data
import com.nasa.pictures.demo.util.DateConverter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * Created by Muthuraj on 17/03/21.
 */
class DetailViewModel @AssistedInject constructor(
    @Assisted data: Data,
    dateConverter: DateConverter
) {
    val placeHolderUrl = data.url
    val hdUrl = data.hdUrl
    val title = data.title
    val explanation = data.explanation
    val publishedDate = dateConverter.formatTime(data.publishedDate, "MMM dd, yyyy")
    val copyrightName = data.copyright
    val copyrightVisibility = data.copyright != null
    val imageTransitionName = data.transitionName

    @AssistedFactory
    interface Factory {
        fun create(data: Data): DetailViewModel
    }
}