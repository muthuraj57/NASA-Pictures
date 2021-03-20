/* $Id$ */
package com.nasa.pictures.demo.ui

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Created by Muthuraj on 20/03/21.
 *
 * This is needed to inject dependencies through Hilt in test module.
 * Ref: https://developer.android.com/training/dependency-injection/hilt-testing#instrumented-tests
 */
class NasaTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}

//@CustomTestApplication(App::class)
//interface HiltTestApplication