/* $Id$ */
package com.nasa.pictures.demo.room

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

/**
 * Created by Muthuraj on 01/06/20.
 *
 * Jambav, Zoho Corporation
 *
 * Same as BaseDao except all functions are suspend functions.
 */
abstract class BaseSuspendDao<T : Any> {
    /*
   * OnConflictStrategy.REPLACE will delete the row before insert on conflicting cases.
   * This will cause to delete all foreign key tables of current table which is not we want.
   * So we insert with OnConflictStrategy.IGNORE, but get the result id. Result id will be -1
   * for conflicting rows. We can identify those rows and call Update on those entries.
   * Refer: https://stackoverflow.com/a/50736568/3423932
   * */

    suspend fun insert(value: T) {
        if (insertOrIgnore(value) == -1L) {
            //Conflict occurred. Update new data.
            update(value)
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertOrIgnore(value: T): Long

    @Update
    abstract suspend fun update(value: T)

    fun insertSync(value: T) {
        if (insertOrIgnoreSync(value) == -1L) {
            //Conflict occurred. Update new data.
            updateSync(value)
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertOrIgnoreSync(value: T): Long

    @Update
    abstract fun updateSync(value: T)

    suspend fun insert(values: List<T>) {
        insertOrIgnore(values)
            .mapIndexedNotNull { index, result ->
                if (result != -1L) {
                    return@mapIndexedNotNull null
                }
                values[index]
            }.takeIf { it.isNotEmpty() }
            ?.let { update(it) }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertOrIgnore(values: List<T>): List<Long>

    @Update
    abstract suspend fun update(values: List<T>)

    abstract suspend fun deleteAll()
}