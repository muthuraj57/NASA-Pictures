/* $Id$ */
package com.nasa.pictures.demo.ui.grid.detail.viewPager

import com.nasa.pictures.demo.model.Data

/**
 * Created by Muthuraj on 17/03/21.
 */
class DetailViewModel(data: Data) {
    val placeHolderUrl = data.url
    val hdUrl = data.hdUrl
    val title = data.title
    val explanation = data.explanation
    val publishedData = data.date
    val copyrightName = data.copyright
    val copyrightVisibility = data.copyright != null
    val imageTransitionName = data.transitionName
}