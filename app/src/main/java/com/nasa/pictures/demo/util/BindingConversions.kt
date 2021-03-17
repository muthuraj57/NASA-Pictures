/* $Id$ */
package com.nasa.pictures.demo.util

import android.view.View
import androidx.databinding.BindingConversion

/**
 * Created by Muthuraj on 17/03/21.
 */
@BindingConversion
fun convertBooleanToVisibility(visible: Boolean) = if (visible) View.VISIBLE else View.GONE