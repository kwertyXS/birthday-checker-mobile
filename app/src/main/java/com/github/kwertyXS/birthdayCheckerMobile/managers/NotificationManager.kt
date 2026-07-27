package com.github.kwertyXS.birthdayCheckerMobile.managers

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.ShaderBrush
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NotificationManager @Inject constructor(
    private val context: Context
) {
    private val sharedPreferences = context.getSharedPreferences("Notification", Context.MODE_PRIVATE)

    fun getData(): Boolean = sharedPreferences.getBoolean("enable", true)
    fun setData(data: Boolean) { sharedPreferences.edit().putBoolean("enable", data).commit()}
}