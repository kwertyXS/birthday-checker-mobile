package com.github.kwertyXS.birthdayCheckerMobile.managers

import android.content.Context

class ContactsPermissionManager(context: Context) {
    private val prefs = context.getSharedPreferences("contacts_permission", Context.MODE_PRIVATE)

    fun isCompleted(): Boolean = prefs.getBoolean("permission_completed", false)

    fun setCompleted() {
        prefs.edit().putBoolean("permission_completed", true).apply()
    }
}
