package com.github.kwertyXS.birthdayCheckerMobile.state

data class AuthState(
    val phone: String = "",
    val verificationCode: String = "",
    val birthday: String = "",
    val isNewUser: Boolean = false,
    val phoneSubmitted: Boolean = false,
    val codeSubmitted: Boolean = false,
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String = "",
)

sealed class AuthEvent {
    data class PhoneChanged(val phone: String) : AuthEvent()
    data class CodeChanged(val code: String) : AuthEvent()
    data class BirthdayChanged(val birthday: String) : AuthEvent()
    object SubmitPhone : AuthEvent()
    object SubmitCode : AuthEvent()
    object SubmitBirthday : AuthEvent()
    object ResetSubmitted : AuthEvent()
    object ClearError : AuthEvent()
    object Reset : AuthEvent()
}
