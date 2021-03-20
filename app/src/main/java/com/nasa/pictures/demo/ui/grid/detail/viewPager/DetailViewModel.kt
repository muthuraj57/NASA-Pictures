/* $Id$ */
package com.nasa.pictures.demo.ui.grid.detail.viewPager

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.nasa.pictures.demo.model.Data

/**
 * Created by Muthuraj on 17/03/21.
 */
class DetailViewModel(data: Data) {
    val url = data.url
    val title = data.title
    val explanation = data.explanation
    val publishedData = data.date
    val copyrightName = data.copyright
    val copyrightVisibility = data.copyright != null
    val imageTransitionName = data.transitionName

    companion object {
        @JvmStatic
        @BindingAdapter("loadUrl")
        fun loadUrl(imageView: ImageView, url: String?) {
            imageView.load(url)
        }
    }
}