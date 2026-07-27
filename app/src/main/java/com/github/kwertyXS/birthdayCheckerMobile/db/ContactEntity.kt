package com.github.kwertyXS.birthdayCheckerMobile.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "contacts_table")
data class ContactEntity (
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val name: String,
    val phone: String,
    val birthday: String
)