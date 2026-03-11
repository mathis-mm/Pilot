package com.example.pilot.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.pilot.data.PilotRepository

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val scheduler = ReminderScheduler(context)
            PilotRepository.reminders.value
                .filter { it.isEnabled && it.triggerTimeMillis > System.currentTimeMillis() }
                .forEach { scheduler.scheduleReminder(it) }
        }
    }
}
