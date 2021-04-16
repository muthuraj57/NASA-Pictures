/* $Id$ */
package com.nasa.pictures.demo.di

import android.content.Context
import androidx.room.Room
import com.nasa.pictures.demo.room.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Muthuraj on 16/04/21.
 *
 * Jambav, Zoho Corporation
 */
@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideYourDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        Database::class.java,
        "nasa_pictures.db"
    ).build()

    @Provides
    fun provideJsonDataDao(database: Database) = database.getJsonDataDao()
}