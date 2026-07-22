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
import java.util.Calendar
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
                    contacts.forEach { c ->
                        Log.d("BirthdaysModel", "  contact: name='${c.name}', birthday='${c.birthday}'")
                    }
                    val grouped = groupBirthdays(contacts)
                    Log.d("BirthdaysModel", "Grouped: yesterday=${grouped.yesterday.size}, today=${grouped.today.size}, tomorrow=${grouped.tomorrow.size}")
                    _state.value = BirthdaysState(groups = grouped, selectedTab = 1)
                },
                onFailure = { e ->
                    Log.e("BirthdaysModel", "Failed to load contacts: ${e.message}")
                    _state.value = BirthdaysState(error = e.message ?: "Failed to load birthdays")
                },
            )
        }
    }

    private fun groupBirthdays(birthdays: List<ContactResponse>): BirthdayGroup {
        val cal = Calendar.getInstance()
        val todayMonth = cal.get(Calendar.MONTH)
        val todayDay = cal.get(Calendar.DAY_OF_MONTH)

        val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }
        val yesterdayMonth = yesterdayCal.get(Calendar.MONTH)
        val yesterdayDay = yesterdayCal.get(Calendar.DAY_OF_MONTH)

        val tomorrowCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }
        val tomorrowMonth = tomorrowCal.get(Calendar.MONTH)
        val tomorrowDay = tomorrowCal.get(Calendar.DAY_OF_MONTH)

        Log.d("BirthdaysModel", "Today: day=$todayDay month=$todayMonth")
        Log.d("BirthdaysModel", "Yesterday: day=$yesterdayDay month=$yesterdayMonth")
        Log.d("BirthdaysModel", "Tomorrow: day=$tomorrowDay month=$tomorrowMonth")

        val yesterday = mutableListOf<ContactResponse>()
        val today = mutableListOf<ContactResponse>()
        val tomorrow = mutableListOf<ContactResponse>()

        for (b in birthdays) {
            val raw = b.birthday ?: ""
            val parts = raw.split(" ", ".", "-", "/")
            Log.d("BirthdaysModel", "Parsing '${b.name}': birthday='$raw' parts=$parts")
            if (parts.size >= 2) {
                var day: Int? = null
                var month: Int? = null
                val first = parts[0].toIntOrNull()
                if (first != null && first > 31) {
                    day = parts[2].toIntOrNull()
                    month = parseMonth(parts[1])
                } else {
                    day = first
                    month = if (day != null) parseMonth(parts[1]) else null
                }
                Log.d("BirthdaysModel", "  parsed: day=$day month=$month (today: day=$todayDay month=$todayMonth)")
                if (day != null && month != null) {
                    when {
                        month == todayMonth && day == todayDay -> { today.add(b); Log.d("BirthdaysModel", "  → today") }
                        month == yesterdayMonth && day == yesterdayDay -> { yesterday.add(b); Log.d("BirthdaysModel", "  → yesterday") }
                        month == tomorrowMonth && day == tomorrowDay -> { tomorrow.add(b); Log.d("BirthdaysModel", "  → tomorrow") }
                        else -> Log.d("BirthdaysModel", "  → no match")
                    }
                }
            }
        }

        return BirthdayGroup(yesterday, today, tomorrow)
    }

    private fun parseMonth(s: String): Int? {
        val months = listOf(
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря",
            "january", "february", "march", "april", "may", "june",
            "july", "august", "september", "october", "november", "december",
            "янв", "фев", "мар", "апр", "май", "июн",
            "июл", "авг", "сен", "окт", "ноя", "дек",
        ).withIndex().groupBy { it.value.lowercase() }.mapValues { it.value.first().index % 12 }

        val m = months[s.lowercase()] ?: s.toIntOrNull()?.let { it - 1 }
        return m
    }
}
