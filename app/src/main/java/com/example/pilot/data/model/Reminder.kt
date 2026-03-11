package com.example.pilot.data.model

enum class ReminderType { TASK, EVENT }

enum class ReminderOffset(val minutes: Int, val label: String) {
    AT_TIME(0, "A l'heure"),
    FIVE_MIN(5, "5 minutes avant"),
    FIFTEEN_MIN(15, "15 minutes avant"),
    THIRTY_MIN(30, "30 minutes avant"),
    ONE_HOUR(60, "1 heure avant"),
    ONE_DAY(1440, "1 jour avant")
}

data class Reminder(
    val id: Long = 0,
    val type: ReminderType,
    val referenceId: Long,
    val title: String,
    val description: String = "",
    val triggerTimeMillis: Long,
    val offset: ReminderOffset = ReminderOffset.AT_TIME,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
