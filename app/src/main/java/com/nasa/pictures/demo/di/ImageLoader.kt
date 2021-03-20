/* $Id$ */
package com.nasa.pictures.demo.di

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Muthuraj on 20/03/21.
 *
 * Currently we use [Glide] for loading images. All usages of Glide should be within this class so
 * that if we decide to switch to other image loading library in future, it would be easier since we
 * have to touch just one file.
 */
@Singleton
class ImageLoader @Inject constructor() {

    @BindingAdapter(value = ["placeHolderUrl", "hdUrl"])
    fun load(imageView: ImageView, placeHolderUrl: String?, hdUrl: String?) {
        if (placeHolderUrl == null || hdUrl == null) {
            //Will be null due to data binding. When the layout using this binding adapter is inflated,
            //dat binding calls this method with extra parameters as null for some reason. Ignore that.
            return
        }
        Glide.with(imageView)
            .load(hdUrl)
            .timeout(TIMEOUT)
            .thumbnail(Glide.with(imageView).load(placeHolderUrl))
            .into(imageView)
    }

    fun load(imageView: ImageView, url: String) {
        Glide.with(imageView)
            .load(url)
            .timeout(TIMEOUT)
            .into(imageView)
    }

    companion object {
        private const val TIMEOUT = 60_000 // 1 minute since NASA image servers are slow.
    }
}