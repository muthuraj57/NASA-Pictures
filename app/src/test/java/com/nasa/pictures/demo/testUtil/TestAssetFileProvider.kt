/* $Id$ */
package com.nasa.pictures.demo.testUtil

import com.nasa.pictures.demo.repository.AssetFileProvider
import java.io.InputStream

/**
 * Created by Muthuraj on 17/03/21.
 *
 * Provides files available from src/main/assets through test classloader context.
 * Note that for this to work, src/main should be added to test source sets through build.gralde.
 */
class TestAssetFileProvider : AssetFileProvider {
    override fun openFile(fileName: String): InputStream {
        return this::class.java.classLoader.getResourceAsStream("assets/data.json")
    }
}