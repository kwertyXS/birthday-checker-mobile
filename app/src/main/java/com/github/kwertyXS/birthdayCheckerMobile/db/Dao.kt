package com.github.kwertyXS.birthdayCheckerMobile.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<ContactEntity>)

    @Query("DELETE FROM contacts_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM contacts_table")
    suspend fun getAll(): List<ContactEntity>
}