package com.example.pilot.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import com.example.pilot.data.model.DeviceCalendarEvent

class CalendarRepository(private val context: Context) {

    fun getEventsForDateRange(startMillis: Long, endMillis: Long): List<DeviceCalendarEvent> {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return emptyList()
        }

        val events = mutableListOf<DeviceCalendarEvent>()
        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.CALENDAR_DISPLAY_NAME,
            CalendarContract.Events.CALENDAR_COLOR,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.EVENT_LOCATION
        )
        val selection = "((${CalendarContract.Events.DTSTART} >= ?) AND " +
                "(${CalendarContract.Events.DTSTART} <= ?))"
        val selectionArgs = arrayOf(startMillis.toString(), endMillis.toString())

        context.contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection, selection, selectionArgs,
            "${CalendarContract.Events.DTSTART} ASC"
        )?.use { cursor ->
            val idIdx = cursor.getColumnIndexOrThrow(CalendarContract.Events._ID)
            val titleIdx = cursor.getColumnIndexOrThrow(CalendarContract.Events.TITLE)
            val descIdx = cursor.getColumnIndexOrThrow(CalendarContract.Events.DESCRIPTION)
            val startIdx = cursor.getColumnIndexOrThrow(CalendarContract.Events.DTSTART)
            val endIdx = cursor.getColumnIndexOrThrow(CalendarContract.Events.DTEND)
            val calNameIdx = cursor.getColumnIndexOrThrow(CalendarContract.Events.CALENDAR_DISPLAY_NAME)
            val calColorIdx = cursor.getColumnIndexOrThrow(CalendarContract.Events.CALENDAR_COLOR)
            val allDayIdx = cursor.getColumnIndexOrThrow(CalendarContract.Events.ALL_DAY)
            val locationIdx = cursor.getColumnIndexOrThrow(CalendarContract.Events.EVENT_LOCATION)

            while (cursor.moveToNext()) {
                events.add(
                    DeviceCalendarEvent(
                        id = cursor.getLong(idIdx),
                        title = cursor.getString(titleIdx) ?: "",
                        description = cursor.getString(descIdx) ?: "",
                        startTime = cursor.getLong(startIdx),
                        endTime = cursor.getLong(endIdx),
                        calendarDisplayName = cursor.getString(calNameIdx) ?: "",
                        calendarColor = cursor.getInt(calColorIdx),
                        isAllDay = cursor.getInt(allDayIdx) == 1,
                        location = cursor.getString(locationIdx) ?: ""
                    )
                )
            }
        }
        return events
    }
}
