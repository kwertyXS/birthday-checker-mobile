package com.github.kwertyXS.birthdayCheckerMobile.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kwertyXS.birthdayCheckerMobile.api.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val phone: String = "",
    val birthday: String = "",
    val isLoading: Boolean = false,
    val error: String = "",
)

@HiltViewModel
class SettingsModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadUser()
    }

    fun loadUser() {
        _state.value = _state.value.copy(isLoading = true, error = "")
        viewModelScope.launch {
            repository.getUser().fold(
                onSuccess = { user ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        phone = user.phone,
                        birthday = user.birthday ?: "",
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load user",
                    )
                },
            )
        }
    }
}
