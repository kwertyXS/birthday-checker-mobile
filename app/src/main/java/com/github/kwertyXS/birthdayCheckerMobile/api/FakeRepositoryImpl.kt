package com.github.kwertyXS.birthdayCheckerMobile.api

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeRepositoryImpl @Inject constructor() : Repository {
    override suspend fun login(phone: String): Result<RefreshTokenResponse> {
        return if (phone.isNotEmpty() && phone.last() == '0') {
            Result.success(RefreshTokenResponse("fake_refresh_token", "fake_access_token"))
        } else {
            Result.failure(Exception("User not found"))
        }
    }

    override suspend fun register(phone: String, birthday: String): Result<RefreshTokenResponse> {
        return Result.success(RefreshTokenResponse("fake_refresh_token", "fake_access_token"))
    }

    override suspend fun refreshToken(token: String): Result<AccessTokenResponse> {
        return Result.success(AccessTokenResponse("fake_access_token"))
    }

    override suspend fun addContact(phone: String, name: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getContacts(): Result<List<ContactResponse>> {
        return Result.success(
            listOf(
                ContactResponse("Иван Иванов", "+7-123-456-78-90", "15 марта 1997"),
                ContactResponse("Мария Петрова", "+7-098-765-43-21", "22 июля 1995"),
            )
        )
    }
}
