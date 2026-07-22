package com.github.kwertyXS.birthdayCheckerMobile.api

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager,
) : Repository {
    override suspend fun login(phone: String): Result<RefreshTokenResponse> = runCatching {
        val response = api.login(LoginRequest(phone))
        tokenManager.saveRefreshToken(response.refreshToken)
        val access = api.refreshToken(response.refreshToken)
        tokenManager.saveAccessToken(access.accessToken)
        response
    }

    override suspend fun register(phone: String, birthday: String): Result<RefreshTokenResponse> = runCatching {
        val response = api.register(RegistrationRequest(phone, birthday))
        tokenManager.saveRefreshToken(response.refreshToken)
        val access = api.refreshToken(response.refreshToken)
        tokenManager.saveAccessToken(access.accessToken)
        response
    }

    override suspend fun refreshToken(token: String): Result<AccessTokenResponse> = runCatching {
        val response = api.refreshToken(token)
        tokenManager.saveAccessToken(response.accessToken)
        response
    }

    override suspend fun addContact(phone: String, name: String): Result<Unit> = runCatching {
        api.addContact(ContactRequest(phone, name))
    }

    override suspend fun getContacts(): Result<List<Any>> = runCatching {
        api.getContacts()
    }
}
