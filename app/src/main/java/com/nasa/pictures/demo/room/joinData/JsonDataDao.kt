/* $Id$ */
package com.nasa.pictures.demo.room.joinData

import androidx.room.Dao
import androidx.room.Query
import com.nasa.pictures.demo.room.BaseSuspendDao

/**
 * Created by Muthuraj on 16/04/21.
 *
 * Jambav, Zoho Corporation
 */
@Dao
abstract class JsonDataDao : BaseSuspendDao<JsonDataEntity>() {

    @Query("DELETE from JsonData")
    abstract override suspend fun deleteAll()
}