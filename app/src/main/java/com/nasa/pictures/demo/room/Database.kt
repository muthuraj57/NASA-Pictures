/* $Id$ */
package com.nasa.pictures.demo.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nasa.pictures.demo.room.joinData.JsonDataDao
import com.nasa.pictures.demo.room.joinData.JsonDataEntity

/**
 * Created by Muthuraj on 16/04/21.
 *
 * Jambav, Zoho Corporation
 */
@Database(entities = [JsonDataEntity::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun getJsonDataDao(): JsonDataDao
}