package com.example.pilot.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pilot.data.PilotRepository
import com.example.pilot.data.model.Event
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = PilotRepository(application)

    val allEvents: StateFlow<List<Event>> = repo.events
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addEvent(title: String, description: String = "", startTime: Long, endTime: Long) {
        viewModelScope.launch {
            repo.addEvent(Event(title = title, description = description, startTime = startTime, endTime = endTime))
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            repo.updateEvent(event)
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            repo.deleteEvent(event)
        }
    }
}
