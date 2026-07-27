package com.github.kwertyXS.birthdayCheckerMobile.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.kwertyXS.birthdayCheckerMobile.api.ContactResponse

@Entity(tableName = "contacts_table")
data class ContactEntity(
    @PrimaryKey val userId: Int,
    val name: String?,
    val phone: String,
    val birthday: String?,
)

fun ContactResponse.toEntity() = ContactEntity(
    userId = userId,
    name = name,
    phone = phone,
    birthday = birthday,
)

fun ContactEntity.toResponse() = ContactResponse(
    userId = userId,
    name = name,
    phone = phone,
    birthday = birthday,
)
