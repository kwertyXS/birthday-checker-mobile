package com.github.kwertyXS.birthdayCheckerMobile.api.repository

import com.github.kwertyXS.birthdayCheckerMobile.api.AccessTokenResponse
import com.github.kwertyXS.birthdayCheckerMobile.api.AddContactResult
import com.github.kwertyXS.birthdayCheckerMobile.api.ApiService
import com.github.kwertyXS.birthdayCheckerMobile.api.ContactRequest
import com.github.kwertyXS.birthdayCheckerMobile.api.ContactResponse
import com.github.kwertyXS.birthdayCheckerMobile.api.DeleteContactRequest
import com.github.kwertyXS.birthdayCheckerMobile.api.DeleteContactResult
import com.github.kwertyXS.birthdayCheckerMobile.api.LoginRequest
import com.github.kwertyXS.birthdayCheckerMobile.api.RefreshTokenResponse
import com.github.kwertyXS.birthdayCheckerMobile.api.RegistrationRequest
import com.github.kwertyXS.birthdayCheckerMobile.api.repository.Repository
import com.github.kwertyXS.birthdayCheckerMobile.managers.TokenManager
import com.github.kwertyXS.birthdayCheckerMobile.api.UserEditRequest
import com.github.kwertyXS.birthdayCheckerMobile.api.UserResponse
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
        if (response.accessToken != null) {
            tokenManager.saveAccessToken(response.accessToken)
        } else {
            val access = api.refreshToken(response.refreshToken)
            tokenManager.saveAccessToken(access.accessToken)
        }
        response
    }

    override suspend fun refreshToken(token: String): Result<AccessTokenResponse> = runCatching {
        val response = api.refreshToken(token)
        tokenManager.saveAccessToken(response.accessToken)
        response
    }

    override suspend fun getUser(): Result<UserResponse> = runCatching {
        api.getUser()
    }

    override suspend fun editUser(body: UserEditRequest): Result<UserResponse> = runCatching {
        api.editUser(body)
    }

    override suspend fun addContacts(contacts: List<ContactRequest>): Result<List<AddContactResult>> = runCatching {
        api.addContact(contacts)
    }

    override suspend fun getContacts(): Result<List<ContactResponse>> = runCatching {
        api.getContacts()
    }

    override suspend fun deleteContacts(contacts: List<DeleteContactRequest>): Result<List<DeleteContactResult>> = runCatching {
        api.deleteContact(contacts)
    }
}