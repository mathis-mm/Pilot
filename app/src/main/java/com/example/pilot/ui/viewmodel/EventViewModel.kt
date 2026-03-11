package com.example.pilot.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pilot.data.PilotRepository
import com.example.pilot.data.model.Event
import kotlinx.coroutines.flow.StateFlow

class EventViewModel : ViewModel() {
    private val repo = PilotRepository

    val allEvents: StateFlow<List<Event>> = repo.events

    fun addEvent(title: String, description: String = "", startTime: Long, endTime: Long) {
        repo.addEvent(Event(title = title, description = description, startTime = startTime, endTime = endTime))
    }

    fun updateEvent(event: Event) {
        repo.updateEvent(event)
    }

    fun deleteEvent(event: Event) {
        repo.deleteEvent(event)
    }
}
