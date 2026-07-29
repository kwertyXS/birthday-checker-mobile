package com.github.kwertyXS.birthdayCheckerMobile.managers

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNotificationManager @Inject constructor(
    private val application: Application,
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences = application.getSharedPreferences("Notification", Context.MODE_PRIVATE)
    private val channelId = "Birthdays"
    private val channel = NotificationChannel(
        channelId,
        "Birthdays",
        IMPORTANCE_HIGH
    )
    private val manager = getSystemService(application, NotificationManager::class.java)

    init {
        manager?.createNotificationChannel(channel)
    }

    fun getData(): Boolean = sharedPreferences.getBoolean("enable", true)
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

    fun addNotification2Queue(date: String, who: String, number: String) {
        if (!getData()) return
        val parts = date.split("-")
        if (parts.size < 3) return
        val month = parts[1].toIntOrNull() ?: return
        val day = parts[2].toIntOrNull() ?: return

        val now = LocalDateTime.now()
        val year = now.year
        var target = LocalDateTime.of(year, month, day, getTimeHour(), getTimeMinute())

        if (target.isBefore(now)) {
            target = target.plusYears(1)
        }

        val triggerMillis = target.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

        val intent = Intent(application, NotificationReceiver::class.java).apply {
            putExtra("date", date)
            putExtra("who", who)
            putExtra("number", number)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            application,
            "${who}_$number".hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val alarmManager = getSystemService(application, AlarmManager::class.java)
//        Toast.makeText(context, "Запланировано на $target", Toast.LENGTH_SHORT).show()
        alarmManager?.setAlarmClock(
            AlarmManager.AlarmClockInfo(triggerMillis, pendingIntent),
            pendingIntent,
        )
    }
}
