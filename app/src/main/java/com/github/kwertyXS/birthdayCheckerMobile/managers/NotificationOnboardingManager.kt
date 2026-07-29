package com.github.kwertyXS.birthdayCheckerMobile.managers

import android.content.Context

class NotificationOnboardingManager(context: Context) {
    private val prefs = context.getSharedPreferences("notification_onboarding", Context.MODE_PRIVATE)

    fun isCompleted(): Boolean = prefs.getBoolean("onboarding_completed", false)

    fun setCompleted() {
        prefs.edit().putBoolean("onboarding_completed", true).apply()
    }
}
