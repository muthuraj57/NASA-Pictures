/* $Id$ */
package com.nasa.pictures.demo.ui.grid.detail.viewPager

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
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

    companion object {
        @JvmStatic
        @BindingAdapter(value = ["placeHolderUrl", "hdUrl"])
        fun loadUrl(imageView: ImageView, placeHolderUrl: String?, hdUrl: String?) {
            if (placeHolderUrl == null || hdUrl == null) {
                return
            }
            Glide.with(imageView)
                .load(hdUrl)
                .timeout(60_000)
                .thumbnail(Glide.with(imageView).load(placeHolderUrl))
                .into(imageView)
        }
    }
}