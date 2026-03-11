package com.example.pilot.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra("reminder_id", -1)
        val title = intent.getStringExtra("title") ?: "Rappel Pilot"
        val description = intent.getStringExtra("description") ?: ""

        NotificationHelper.showReminderNotification(
            context, reminderId.toInt(), title, description
        )
    }
}
