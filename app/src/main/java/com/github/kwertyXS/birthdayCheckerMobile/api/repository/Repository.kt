package com.github.kwertyXS.birthdayCheckerMobile.api.repository

import com.github.kwertyXS.birthdayCheckerMobile.api.AccessTokenResponse
import com.github.kwertyXS.birthdayCheckerMobile.api.AddContactResult
import com.github.kwertyXS.birthdayCheckerMobile.api.ContactRequest
import com.github.kwertyXS.birthdayCheckerMobile.api.ContactResponse
import com.github.kwertyXS.birthdayCheckerMobile.api.RefreshTokenResponse
import com.github.kwertyXS.birthdayCheckerMobile.api.UserEditRequest
import com.github.kwertyXS.birthdayCheckerMobile.api.UserResponse

interface Repository {
    suspend fun login(phone: String): Result<RefreshTokenResponse>
    suspend fun register(phone: String, birthday: String): Result<RefreshTokenResponse>
    suspend fun refreshToken(token: String): Result<AccessTokenResponse>
    suspend fun getUser(): Result<UserResponse>
    suspend fun editUser(body: UserEditRequest): Result<UserResponse>
    suspend fun addContacts(contacts: List<ContactRequest>): Result<List<AddContactResult>>
    suspend fun getContacts(): Result<List<ContactResponse>>
    suspend fun deleteContact(contactId: Int): Result<Unit>
}