package com.github.kwertyXS.birthdayCheckerMobile.api

import com.google.gson.annotations.SerializedName

// ─── Requests ─────────────────────────────────────────────────────────────────

data class LoginRequest(
    val phone: String,
)

data class RegistrationRequest(
    val phone: String,
    val birthday: String,
)

data class ContactRequest(
    val phone: String,
    val name: String,
)

// ─── Responses ────────────────────────────────────────────────────────────────

data class RefreshTokenResponse(
    @SerializedName("refresh_token")
    val refreshToken: String = "",
    @SerializedName("access_token")
    val accessToken: String? = null,
)

data class AccessTokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
)

// ─── Contact Response ─────────────────────────────────────────────────────────

data class ContactResponse(
    val name: String = "",
    val phone: String = "",
    val birthday: String = "",
)

// ─── Error ────────────────────────────────────────────────────────────────────

data class ValidationError(
    val loc: List<Any>,
    val msg: String,
    val type: String,
)

data class HttpValidationError(
    val detail: List<ValidationError>?,
)
