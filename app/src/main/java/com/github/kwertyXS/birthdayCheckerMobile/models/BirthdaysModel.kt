package com.github.kwertyXS.birthdayCheckerMobile.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kwertyXS.birthdayCheckerMobile.api.ContactResponse
import com.github.kwertyXS.birthdayCheckerMobile.api.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BirthdayGroup(
    val yesterday: List<ContactResponse> = emptyList(),
    val today: List<ContactResponse> = emptyList(),
    val tomorrow: List<ContactResponse> = emptyList(),
)

data class BirthdaysState(
    val groups: BirthdayGroup = BirthdayGroup(),
    val selectedTab: Int = 1,
    val isLoading: Boolean = false,
    val error: String = "",
)

@HiltViewModel
class BirthdaysModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    private val _state = MutableStateFlow(BirthdaysState())
    val state: StateFlow<BirthdaysState> = _state.asStateFlow()

    init {
        loadBirthdays()
    }

    fun selectTab(index: Int) {
        _state.value = _state.value.copy(selectedTab = index)
    }

    fun loadBirthdays() {
        _state.value = _state.value.copy(isLoading = true, error = "")
        viewModelScope.launch {
            repository.getContacts().fold(
                onSuccess = { contacts ->
                    Log.d("BirthdaysModel", "Got ${contacts.size} contacts from API")
                    _state.value = BirthdaysState(groups = BirthdayGroup(), selectedTab = 1)
                },
                onFailure = { e ->
                    Log.e("BirthdaysModel", "Failed to load contacts: ${e.message}")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load birthdays",
                    )
                },
            )
        }
    }
}
