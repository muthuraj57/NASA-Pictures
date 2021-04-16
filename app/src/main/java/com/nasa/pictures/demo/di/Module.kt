/* $Id$ */
package com.nasa.pictures.demo.di

import android.content.Context
import androidx.room.Room
import com.nasa.pictures.demo.repository.AssetFileProvider
import com.nasa.pictures.demo.repository.AssetFileProviderImpl
import com.nasa.pictures.demo.room.Database
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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

    @Singleton
    @Provides
    fun provideYourDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        Database::class.java,
        "nasa_pictures.db"
    ).build()

}