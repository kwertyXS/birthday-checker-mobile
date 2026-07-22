package com.github.kwertyXS.birthdayCheckerMobile.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("/api/v1/registration")
    suspend fun register(@Body body: RegistrationRequest): RefreshTokenResponse

    @POST("/api/v1/login")
    suspend fun login(@Body body: LoginRequest): RefreshTokenResponse

    @POST("/api/v1/refresh")
    suspend fun refreshToken(@Query("token") token: String): AccessTokenResponse

    @POST("/api/v1/add")
    suspend fun addContact(@Body body: ContactRequest): Unit

    @GET("/api/v1/contacts")
    suspend fun getContacts(): List<Any>
}
