package com.github.kwertyXS.birthdayCheckerMobile.models

import android.app.Application
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.kwertyXS.birthdayCheckerMobile.api.AddContactResult
import com.github.kwertyXS.birthdayCheckerMobile.api.ContactRequest
import com.github.kwertyXS.birthdayCheckerMobile.api.ContactResponse
import com.github.kwertyXS.birthdayCheckerMobile.api.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class ContactInfo(
    val userId: Int = 0,
    val fullName: String = "",
    val phone: String = "",
)

data class ContactsState(
    val contacts: List<ContactInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = "",
)

@HiltViewModel
class ContactsModel @Inject constructor(
    private val repository: Repository,
    private val application: Application,
) : AndroidViewModel(application) {
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
                            ContactInfo(userId = it.userId, fullName = it.name ?: "", phone = it.phone)
                        },
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load contacts",
                    )
                },
            )
        }
    }

    fun deleteContact(contactId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.deleteContact(contactId).fold(
                onSuccess = { loadContacts() },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to delete contact",
                    )
                },
            )
        }
    }

    fun syncContacts() {
        _state.value = _state.value.copy(isLoading = true, error = "")
        viewModelScope.launch {
            val deviceContacts = withContext(Dispatchers.IO) {
                val list = mutableListOf<ContactRequest>()
                val cursor = application.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ),
                    null, null, null
                )
                cursor?.use { c ->
                    val nameIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val phoneIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    while (c.moveToNext()) {
                        val name = c.getString(nameIdx)?.takeIf { it.isNotBlank() } ?: continue
                        var phone = c.getString(phoneIdx)?.takeIf { it.isNotBlank() } ?: continue
                        if (phone.count { it.isDigit() } < 4) continue
                        phone = phone
                            .filter { char -> char !in arrayOf('-', ' ', '(', ')') }

                        if ("*" in phone || "#" in phone) continue

                        // обработка "тупых" номеров
                        if (phone.startsWith("8")) {
                            phone.subSequence(1, phone.length)
                            phone = "+7$phone"
                        }
                        if (phone.startsWith("80")){
                            phone.subSequence(2, phone.length)
                            phone = "+375$phone"
                        }
                        list.add(ContactRequest(phone, name))
                    }
                }
                list
            }

            repository.addContacts(deviceContacts).fold(
                onSuccess = {
                    loadContacts()
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to sync contacts",
                    )
                },
            )
        }
    }

    fun addContact(name: String, phone: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val request = listOf(ContactRequest(phone, name))
            repository.addContacts(request).fold(
                onSuccess = { results ->
                    val ok = results.any { it.status == "ok" }
                    if (ok) {
                        loadContacts()
                    }
                    onResult(ok)
                },
                onFailure = {
                    onResult(false)
                },
            )
        }
    }
}
