/* $Id$ */
package com.nasa.pictures.demo.util

import android.widget.ImageView
import com.nasa.pictures.demo.model.Data

/**
 * Created by Muthuraj on 17/03/21.
 */
object TransitionNameSetter {
    fun set(view: ImageView, data: Data) {
        view.transitionName = getTransitionName(data)
    }

    fun getTransitionName(data: Data) = data.title + data.url
}