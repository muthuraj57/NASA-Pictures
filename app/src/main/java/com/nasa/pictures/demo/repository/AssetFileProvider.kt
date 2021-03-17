/* $Id$ */
package com.nasa.pictures.demo.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream
import javax.inject.Inject

/**
 * Created by Muthuraj on 17/03/21.
 *
 * Getting file from asset requires application context. So we extract this task into an interface,
 * so that we can give fake implementation in unit test.
 */
interface AssetFileProvider {
    fun openFile(fileName: String): InputStream
}

class AssetFileProviderImpl @Inject constructor(@ApplicationContext private val context: Context) :
    AssetFileProvider {
    override fun openFile(fileName: String): InputStream {
        return context.assets.open(fileName)
    }
}