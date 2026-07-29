package com.github.kwertyXS.birthdayCheckerMobile.managers

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.kwertyXS.birthdayCheckerMobile.R

class NotificationReceiver : BroadcastReceiver() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val prefs = context.getSharedPreferences("Notification", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("enable", true)) return

        val date = intent.getStringExtra("date") ?: return
        val who = intent.getStringExtra("who") ?: return
        val number = intent.getStringExtra("number") ?: return

        val callIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$number")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            callIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, "Birthdays")
            .setContentTitle(context.getString(R.string.near_birthday))
            .setContentText(context.getString(R.string.today_birthday) + who)
            .addAction(R.drawable.ic_call, "Позвонить", pendingIntent)
            .setSmallIcon(R.drawable.ic_bell)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(date.hashCode(), notification)
//        Toast.makeText(context, "Уведомление для $who", Toast.LENGTH_LONG).show()
    }
}
