package com.github.kwertyXS.birthdayCheckerMobile.api

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiRepositoryImpl @Inject constructor(
    private val api: ApiService,
) : Repository {
    override suspend fun login(phone: String): Result<RefreshTokenResponse> = runCatching {
        api.login(LoginRequest(phone))
    }

    override suspend fun register(phone: String, birthday: String): Result<RefreshTokenResponse> = runCatching {
        api.register(RegistrationRequest(phone, birthday))
    }

    override suspend fun refreshToken(token: String): Result<AccessTokenResponse> = runCatching {
        api.refreshToken(token)
    }

    override suspend fun addContact(phone: String, name: String): Result<Unit> = runCatching {
        api.addContact(ContactRequest(phone, name))
    }

    override suspend fun getContacts(): Result<List<Any>> = runCatching {
        api.getContacts()
    }
}
