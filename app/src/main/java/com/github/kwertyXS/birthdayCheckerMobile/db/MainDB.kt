package com.github.kwertyXS.birthdayCheckerMobile.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ContactEntity::class
    ],
    version = 1
)
abstract class MainDB : RoomDatabase() {
    abstract val dao: Dao
}
