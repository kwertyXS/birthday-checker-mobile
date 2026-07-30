package com.github.kwertyXS.birthdayCheckerMobile.managers

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val workManager = WorkManager.getInstance(context)

    fun scheduleDailyCheck() {
        val prefs = context.getSharedPreferences("Notification", Context.MODE_PRIVATE)
        val hour = prefs.getInt("hour", 17)
        val minute = prefs.getInt("minute", 0)

        val now = LocalDateTime.now()
        var target = LocalDateTime.of(now.year, now.month, now.dayOfMonth, hour, minute)
        if (!target.isAfter(now)) target = target.plusDays(1)

        val initialDelay = Duration.between(now, target).toMillis()

        val request = OneTimeWorkRequestBuilder<BirthdayWorker>()
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniqueWork(
            "birthday_check",
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    fun cancelDailyCheck() {
        workManager.cancelUniqueWork("birthday_check")
    }
}
