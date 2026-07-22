package com.github.kwertyXS.birthdayCheckerMobile.api

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeRepositoryImpl @Inject constructor() : Repository {
    override suspend fun login(phone: String): Result<RefreshTokenResponse> {
        return if (phone.isNotEmpty() && phone.last() == '0') {
            Result.success(RefreshTokenResponse("fake_refresh_token"))
        } else {
            Result.failure(Exception("User not found"))
        }
    }

    override suspend fun register(phone: String, birthday: String): Result<RefreshTokenResponse> {
        return Result.success(RefreshTokenResponse("fake_refresh_token"))
    }

    override suspend fun refreshToken(token: String): Result<AccessTokenResponse> {
        return Result.success(AccessTokenResponse("fake_access_token"))
    }

    override suspend fun addContact(phone: String, name: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getContacts(): Result<List<Any>> {
        return Result.success(emptyList())
    }
}
