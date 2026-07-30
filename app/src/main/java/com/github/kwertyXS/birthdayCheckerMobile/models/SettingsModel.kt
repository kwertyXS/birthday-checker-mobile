package com.github.kwertyXS.birthdayCheckerMobile.models

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kwertyXS.birthdayCheckerMobile.api.UserEditRequest
import com.github.kwertyXS.birthdayCheckerMobile.api.repository.Repository
import com.github.kwertyXS.birthdayCheckerMobile.managers.AppNotificationManager
import com.github.kwertyXS.birthdayCheckerMobile.managers.NotificationScheduler
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
    val notificationsEnabled: Boolean = true,
    val notificationHour: Int = 17,
    val notificationMinute: Int = 0,
)

@HiltViewModel
class SettingsModel @Inject constructor(
    private val repository: Repository,
    private val notificationManager: AppNotificationManager,
    private val notificationScheduler: NotificationScheduler,
    private val application: Application,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        _state.value = _state.value.copy(
            notificationsEnabled = notificationManager.getData(),
            notificationHour = notificationManager.getTimeHour(),
            notificationMinute = notificationManager.getTimeMinute(),
        )
        if (notificationManager.getData()) {
            notificationScheduler.scheduleDailyCheck()
        }
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

    fun refreshNotificationState() {
        _state.value = _state.value.copy(
            notificationsEnabled = notificationManager.getData(),
        )
    }

    fun toggleNotifications() {
        val newValue = !_state.value.notificationsEnabled
        notificationManager.setData(newValue)
        _state.value = _state.value.copy(notificationsEnabled = newValue)
        if (newValue) {
            notificationScheduler.scheduleDailyCheck()
            Toast.makeText(
                application,
                "Уведомления включены",
                Toast.LENGTH_SHORT,
            ).show()
        } else {
            notificationScheduler.cancelDailyCheck()
            Toast.makeText(
                application,
                "Уведомления выключены",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    fun updateNotificationTime(hour: Int, minute: Int) {
        notificationManager.setTimeHour(hour)
        notificationManager.setTimeMinute(minute)
        _state.value = _state.value.copy(notificationHour = hour, notificationMinute = minute)
        if (notificationManager.getData()) {
            notificationScheduler.scheduleDailyCheck()
            Toast.makeText(
                application,
                String.format("Время уведомлений: %02d:%02d", hour, minute),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    fun updateBirthday(newBirthday: String) {
        _state.value = _state.value.copy(error = "")
        viewModelScope.launch {
            repository.editUser(UserEditRequest(birthday = newBirthday)).fold(
                onSuccess = {
                    _state.value = _state.value.copy(birthday = newBirthday)
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(
                        error = e.message ?: "Failed to update birthday",
                    )
                },
            )
        }
    }
}
