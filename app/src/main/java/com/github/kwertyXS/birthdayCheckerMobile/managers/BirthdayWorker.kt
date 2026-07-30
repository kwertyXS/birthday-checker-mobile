package com.github.kwertyXS.birthdayCheckerMobile.managers

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.github.kwertyXS.birthdayCheckerMobile.R
import com.github.kwertyXS.birthdayCheckerMobile.db.WorkerEntryPoint
import dagger.hilt.android.EntryPointAccessors
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class BirthdayWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            doWorkInternal()
            scheduleNext()
            Result.success()
        } catch (e: Exception) {
            Log.e("BirthdayWorker", "crash in doWork", e)
            Result.failure()
        }
    }

    private suspend fun doWorkInternal() {
        val prefs = applicationContext.getSharedPreferences("Notification", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("enable", false)) return

        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext, WorkerEntryPoint::class.java
        )
        val dao = entryPoint.dao()
        val application = entryPoint.application()

        val contacts = dao.getAll()
        val today = LocalDate.now()

        contacts.forEach { contact ->
            val birthday = contact.birthday ?: return@forEach
            val parts = birthday.split("-")
            if (parts.size < 3) return@forEach
            val month = parts[1].toIntOrNull() ?: return@forEach
            val day = parts[2].toIntOrNull() ?: return@forEach

            if (month == today.monthValue && day == today.dayOfMonth) {
                showNotification(application, contact.name ?: "", contact.phone, birthday)
            }
        }
    }

    private fun scheduleNext() {
        val prefs = applicationContext.getSharedPreferences("Notification", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("enable", false)) return

        val hour = prefs.getInt("hour", 17)
        val minute = prefs.getInt("minute", 0)
        val now = LocalDateTime.now()
        var target = LocalDateTime.of(now.year, now.month, now.dayOfMonth, hour, minute)
        if (!target.isAfter(now)) target = target.plusDays(1)

        val delay = Duration.between(now, target).toMillis()

        val request = OneTimeWorkRequestBuilder<BirthdayWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork("birthday_check", ExistingWorkPolicy.REPLACE, request)
    }

    private fun showNotification(application: Context, name: String, phone: String, date: String) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(application, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        val callIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }
        val pendingIntent = PendingIntent.getActivity(
            application, 0, callIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(application, "Birthdays")
            .setContentTitle(application.getString(R.string.near_birthday))
            .setContentText(application.getString(R.string.today_birthday) + " " + name)
            .addAction(R.drawable.ic_call, "Позвонить", pendingIntent)
            .setSmallIcon(R.drawable.ic_bell)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(application).notify(date.hashCode(), notification)
    }
}
