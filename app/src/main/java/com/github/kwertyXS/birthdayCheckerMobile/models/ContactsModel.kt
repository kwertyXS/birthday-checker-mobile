package com.github.kwertyXS.birthdayCheckerMobile.models

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

data class ContactInfo(
    val fullName: String = "",
    val birthday: String = "",
)

data class ContactsState(
    val contacts: List<ContactInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = "",
)

@HiltViewModel
class ContactsModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    private val _state = MutableStateFlow(ContactsState())
    val state: StateFlow<ContactsState> = _state.asStateFlow()

    init {
        loadContacts()
    }

    fun loadContacts() {
        _state.value = _state.value.copy(isLoading = true, error = "")
        viewModelScope.launch {
            repository.getContacts().fold(
                onSuccess = { contacts ->
                    _state.value = ContactsState(
                        contacts = contacts.map {
                            ContactInfo(fullName = it.name ?: "", birthday = it.birthday ?: "")
                        },
                    )
                },
                onFailure = { e ->
                    _state.value = ContactsState(error = e.message ?: "Failed to load contacts")
                },
            )
        }
    }

    fun addContact(name: String, phone: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.addContact(phone, name).fold(
                onSuccess = {
                    loadContacts()
                    onResult(true)
                },
                onFailure = {
                    onResult(false)
                },
            )
        }
    }
}
