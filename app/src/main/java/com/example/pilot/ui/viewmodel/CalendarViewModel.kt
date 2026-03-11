package com.example.pilot.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pilot.data.CalendarRepository
import com.example.pilot.data.model.DeviceCalendarEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val calendarRepo = CalendarRepository(application)

    private val _deviceEvents = MutableStateFlow<List<DeviceCalendarEvent>>(emptyList())
    val deviceEvents: StateFlow<List<DeviceCalendarEvent>> = _deviceEvents.asStateFlow()

    private val _upcomingEvents = MutableStateFlow<List<DeviceCalendarEvent>>(emptyList())
    val upcomingEvents: StateFlow<List<DeviceCalendarEvent>> = _upcomingEvents.asStateFlow()

    fun loadEventsForDate(year: Int, month: Int, dayOfMonth: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val cal = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val dayStart = cal.timeInMillis
            val dayEnd = dayStart + 86_400_000L
            _deviceEvents.value = calendarRepo.getEventsForDateRange(dayStart, dayEnd)
        }
    }

    fun loadUpcomingEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val sevenDaysLater = now + (7 * 86_400_000L)
            _upcomingEvents.value = calendarRepo.getEventsForDateRange(now, sevenDaysLater)
        }
    }
}
