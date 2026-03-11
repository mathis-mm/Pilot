package com.example.pilot.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.pilot.data.PilotRepository
import com.example.pilot.data.model.Reminder
import com.example.pilot.data.model.ReminderOffset
import com.example.pilot.data.model.ReminderType
import com.example.pilot.notifications.ReminderScheduler
import kotlinx.coroutines.flow.StateFlow

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = PilotRepository
    private val scheduler = ReminderScheduler(application)

    val reminders: StateFlow<List<Reminder>> = repo.reminders

    fun createReminder(
        type: ReminderType,
        referenceId: Long,
        title: String,
        description: String = "",
        eventTimeMillis: Long,
        offset: ReminderOffset
    ) {
        val triggerTime = eventTimeMillis - (offset.minutes * 60 * 1000L)
        if (triggerTime <= System.currentTimeMillis()) return

        val reminder = Reminder(
            type = type,
            referenceId = referenceId,
            title = title,
            description = description,
            triggerTimeMillis = triggerTime,
            offset = offset
        )
        val id = repo.addReminder(reminder)
        scheduler.scheduleReminder(reminder.copy(id = id))
    }

    fun cancelReminder(reminderId: Long) {
        scheduler.cancelReminder(reminderId)
        repo.deleteReminder(reminderId)
    }
}
