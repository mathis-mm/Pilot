package com.example.pilot.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.pilot.data.PilotDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val scheduler = ReminderScheduler(context)
            val db = PilotDatabase.getInstance(context)
            CoroutineScope(Dispatchers.IO).launch {
                db.reminderDao().getAll().first()
                    .filter { it.isEnabled && it.triggerTimeMillis > System.currentTimeMillis() }
                    .forEach { scheduler.scheduleReminder(it) }
            }
        }
    }
}
