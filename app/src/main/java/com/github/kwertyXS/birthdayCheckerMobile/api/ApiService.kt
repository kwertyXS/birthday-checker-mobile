package com.github.kwertyXS.birthdayCheckerMobile.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("/api/v1/registration")
    suspend fun register(@Body body: RegistrationRequest): RefreshTokenResponse

    @POST("/api/v1/login")
    suspend fun login(@Body body: LoginRequest): RefreshTokenResponse

    @POST("/api/v1/refresh")
    suspend fun refreshToken(@Query("token") token: String): AccessTokenResponse

    @GET("/api/v1/user")
    suspend fun getUser(): UserResponse

    @PATCH("/api/v1/user")
    suspend fun editUser(@Body body: UserEditRequest): UserResponse

    @POST("/api/v1/contacts")
    suspend fun addContact(@Body body: ContactRequest): Unit

    @GET("/api/v1/contacts")
    suspend fun getContacts(): List<ContactResponse>

    @DELETE("/api/v1/contacts")
    suspend fun deleteContact(@Query("contact_id") contactId: Int): Unit
}
