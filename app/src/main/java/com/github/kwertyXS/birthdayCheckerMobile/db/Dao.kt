package com.github.kwertyXS.birthdayCheckerMobile.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface Dao {
    @Insert
    suspend fun insertItem(contactEntity: ContactEntity)

    @Delete
    suspend fun deleteItem(contactEntity: ContactEntity)

    @Update
    suspend fun updateItem(contactEntity: ContactEntity)

    @Query("SELECT * FROM contacts_table")
    fun getAllItems(): Flow<List<ContactEntity>>
}