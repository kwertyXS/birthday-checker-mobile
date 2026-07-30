package com.github.kwertyXS.birthdayCheckerMobile.managers

import android.app.Application
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNotificationManager @Inject constructor(
    private val application: Application,
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences = application.getSharedPreferences("Notification", Context.MODE_PRIVATE)

    fun getData(): Boolean = sharedPreferences.getBoolean("enable", false)
    fun setData(data: Boolean) {
        sharedPreferences.edit().putBoolean("enable", data).commit()
    }

    fun getTimeHour(): Int = sharedPreferences.getInt("hour", 17)
    fun setTimeHour(hour: Int) {
        sharedPreferences.edit().putInt("hour", hour).apply()
    }

    fun getTimeMinute(): Int = sharedPreferences.getInt("minute", 0)
    fun setTimeMinute(minute: Int) {
        sharedPreferences.edit().putInt("minute", minute).apply()
    }
}
