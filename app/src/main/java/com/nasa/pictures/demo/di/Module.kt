/* $Id$ */
package com.nasa.pictures.demo.di

import com.nasa.pictures.demo.repository.AssetFileProvider
import com.nasa.pictures.demo.repository.AssetFileProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by Muthuraj on 17/03/21.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class Module {
    @Binds
    abstract fun bindDispatchers(dispatcherProvider: DispatcherProviderImpl): DispatcherProvider

    @Binds
    abstract fun bindAssetFileProvider(assetFileProviderImpl: AssetFileProviderImpl): AssetFileProvider
}