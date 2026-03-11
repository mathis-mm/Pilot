package com.example.pilot.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pilot.MainActivity
import com.example.pilot.R

object NotificationHelper {
    const val CHANNEL_REMINDERS = "pilot_reminders"
    const val CHANNEL_UPDATES = "pilot_updates"
    private const val UPDATE_NOTIF_ID = 9999

    fun createNotificationChannels(context: Context) {
        val soundUri = Uri.parse("android.resource://${context.packageName}/${R.raw.notif_sound}")
        val audioAttr = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val reminderChannel = NotificationChannel(
            CHANNEL_REMINDERS,
            "Rappels",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Rappels pour vos taches et evenements"
            setSound(soundUri, audioAttr)
            enableVibration(true)
            enableLights(true)
            lightColor = 0xFF6C63FF.toInt()
        }

        val updateChannel = NotificationChannel(
            CHANNEL_UPDATES,
            "Mises a jour",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications de mise a jour de l'application"
            setSound(soundUri, audioAttr)
            enableVibration(true)
            enableLights(true)
            lightColor = 0xFF6C63FF.toInt()
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(reminderChannel)
        manager.createNotificationChannel(updateChannel)
    }

    fun showReminderNotification(context: Context, reminderId: Int, title: String, body: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, reminderId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(R.drawable.pilot_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(reminderId, notification)
        } catch (_: SecurityException) { }
    }

    fun showUpdateNotification(context: Context, versionName: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, UPDATE_NOTIF_ID, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_UPDATES)
            .setSmallIcon(R.drawable.pilot_logo)
            .setContentTitle("Mise a jour disponible")
            .setContentText("Pilot $versionName est disponible. Ouvrez l'app pour mettre a jour.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(UPDATE_NOTIF_ID, notification)
        } catch (_: SecurityException) { }
    }
}
