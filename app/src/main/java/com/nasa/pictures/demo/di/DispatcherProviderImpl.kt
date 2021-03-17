/* $Id$ */
package com.nasa.pictures.demo.di

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Created by Muthuraj on 17/03/21.
 *
 * Provide Kotlin coroutine's dispatcher through this interface so that we can give fake dispatcher
 * while unit testing.
 * Inspired from [https://craigrussell.io/2019/11/unit-testing-coroutine-suspend-functions-using-testcoroutinedispatcher/]
 */
interface DispatcherProvider {
    val Default
        get() = Dispatchers.Default
    val Main
        get() = Dispatchers.Main
    val IO
        get() = Dispatchers.IO
    val Unconfined
        get() = Dispatchers.Unconfined
}

class DispatcherProviderImpl @Inject constructor() : DispatcherProvider