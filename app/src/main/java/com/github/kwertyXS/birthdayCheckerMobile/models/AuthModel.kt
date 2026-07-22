package com.github.kwertyXS.birthdayCheckerMobile.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kwertyXS.birthdayCheckerMobile.api.Repository
import com.github.kwertyXS.birthdayCheckerMobile.api.TokenManager
import com.github.kwertyXS.birthdayCheckerMobile.state.AuthEvent
import com.github.kwertyXS.birthdayCheckerMobile.state.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthModel @Inject constructor(
    private val repository: Repository,
    private val tokenManager: TokenManager,
) : ViewModel() {
    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.PhoneChanged -> _state.update { it.copy(phone = event.phone, error = "") }
            is AuthEvent.CodeChanged -> _state.update { it.copy(verificationCode = event.code, error = "") }
            is AuthEvent.BirthdayChanged -> _state.update { it.copy(birthday = event.birthday) }
            is AuthEvent.SubmitPhone -> {
                val phone = _state.value.phone
                _state.update { it.copy(isLoading = true, error = "") }
                viewModelScope.launch {
                    val result = repository.login(phone)
                    result.fold(
                        onSuccess = {
                            _state.update { it.copy(isLoading = false, isNewUser = false, phoneSubmitted = true) }
                        },
                        onFailure = {
                            _state.update { it.copy(isLoading = false, isNewUser = true, phoneSubmitted = true) }
                        },
                    )
                }
            }
            is AuthEvent.SubmitCode -> {
                _state.update { it.copy(isLoading = true, error = "") }
                viewModelScope.launch {
                    val result = repository.login(_state.value.phone)
                    result.fold(
                        onSuccess = {
                            _state.update { it.copy(isLoading = false, isAuthenticated = true, codeSubmitted = true) }
                        },
                        onFailure = {
                            _state.update { it.copy(isLoading = false, codeSubmitted = true) }
                        },
                    )
                }
            }
            is AuthEvent.SubmitBirthday -> {
                _state.update { it.copy(isLoading = true, error = "") }
                viewModelScope.launch {
                    val result = repository.register(_state.value.phone, _state.value.birthday)
                    result.fold(
                        onSuccess = {
                            _state.update { it.copy(isLoading = false, isAuthenticated = true) }
                        },
                        onFailure = { e ->
                            _state.update { it.copy(isLoading = false, error = e.message ?: "Registration failed") }
                        },
                    )
                }
            }
            is AuthEvent.ResetSubmitted -> _state.update { it.copy(phoneSubmitted = false, codeSubmitted = false) }
            is AuthEvent.ClearError -> _state.update { it.copy(error = "") }
            is AuthEvent.Reset -> _state.value = AuthState()
            is AuthEvent.Logout -> {
                tokenManager.clear()
                _state.value = AuthState()
            }
        }
    }
}
