package com.github.kwertyXS.birthdayCheckerMobile.models

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kwertyXS.birthdayCheckerMobile.api.ContactResponse
import com.github.kwertyXS.birthdayCheckerMobile.api.repository.Repository
import com.github.kwertyXS.birthdayCheckerMobile.db.Dao
import com.github.kwertyXS.birthdayCheckerMobile.db.toEntity
import com.github.kwertyXS.birthdayCheckerMobile.db.toResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
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

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class BirthdaysModel @Inject constructor(
    private val repository: Repository,
    private val dao: Dao,
) : ViewModel() {
    private val _state = MutableStateFlow(BirthdaysState())
    val state: StateFlow<BirthdaysState> = _state.asStateFlow()

    fun selectTab(index: Int) {
        _state.value = _state.value.copy(selectedTab = index)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadBirthdays() {
        _state.value = _state.value.copy(isLoading = true, error = "")
        viewModelScope.launch {
            repository.getContacts().fold(
                onSuccess = { contacts ->
                    withContext(Dispatchers.IO) {
                        dao.deleteAll()
                        dao.insertAll(contacts.map { it.toEntity() })
                    }
                    applyGroupedState(contacts)
                },
                onFailure = { e ->
                    Log.e("BirthdaysModel", "Failed to load contacts: ${e.message}")
                    val cached = withContext(Dispatchers.IO) { dao.getAll() }
                    if (cached.isNotEmpty()) {
                        applyGroupedState(cached.map { it.toResponse() })
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load birthdays",
                        )
                    }
                },
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun applyGroupedState(contacts: List<ContactResponse>) {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val tomorrow = today.plusDays(1)

        val grouped = contacts.groupBy { contact ->
            val bday = contact.birthday?.let {
                try { LocalDate.parse(it) } catch (_: Exception) { null }
            } ?: return@groupBy null
            val key = bday.monthValue * 100 + bday.dayOfMonth
            val todayKey = today.monthValue * 100 + today.dayOfMonth
            val yesterdayKey = yesterday.monthValue * 100 + yesterday.dayOfMonth
            val tomorrowKey = tomorrow.monthValue * 100 + tomorrow.dayOfMonth
            when (key) {
                todayKey -> 0
                yesterdayKey -> -1
                tomorrowKey -> 1
                else -> null
            }
        }

        _state.value = BirthdaysState(
            groups = BirthdayGroup(
                yesterday = grouped[-1] ?: emptyList(),
                today = grouped[0] ?: emptyList(),
                tomorrow = grouped[1] ?: emptyList(),
            ),
            isLoading = false,
        )
    }
}
