/* $Id$ */
package com.nasa.pictures.demo.testUtil

import com.nasa.pictures.demo.di.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain

/**
 * Created by Muthuraj on 17/03/21.
 */
class TestDispatchers : DispatcherProvider {
    init {
        Dispatchers.setMain(TestCoroutineDispatcher())
    }

    override val IO: CoroutineDispatcher
        get() = Main
    override val Default: CoroutineDispatcher
        get() = Main
    override val Unconfined: CoroutineDispatcher
        get() = Main
}