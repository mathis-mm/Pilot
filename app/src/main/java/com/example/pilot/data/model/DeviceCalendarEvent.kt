package com.example.pilot.data.model

data class DeviceCalendarEvent(
    val id: Long,
    val title: String,
    val description: String = "",
    val startTime: Long,
    val endTime: Long,
    val calendarDisplayName: String = "",
    val calendarColor: Int = 0xFF6C63FF.toInt(),
    val isAllDay: Boolean = false,
    val location: String = ""
)
