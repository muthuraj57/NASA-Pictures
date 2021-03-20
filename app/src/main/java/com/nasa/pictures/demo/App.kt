/* $Id$ */
package com.nasa.pictures.demo

import android.app.Application
import androidx.databinding.DataBindingUtil
import com.nasa.pictures.demo.di.AppDataBindingComponent
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Created by Muthuraj on 17/03/21.
 */
@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var appDataBindingComponent: AppDataBindingComponent

    override fun onCreate() {
        super.onCreate()

        //To use our custom binding adapters.
        DataBindingUtil.setDefaultComponent(appDataBindingComponent)
    }
}