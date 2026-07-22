package com.github.kwertyXS.birthdayCheckerMobile.api

interface Repository {
    suspend fun login(phone: String): Result<RefreshTokenResponse>
    suspend fun register(phone: String, birthday: String): Result<RefreshTokenResponse>
    suspend fun refreshToken(token: String): Result<AccessTokenResponse>
    suspend fun addContact(phone: String, name: String): Result<Unit>
    suspend fun getContacts(): Result<List<Any>>
}