package com.example.pilot.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pilot.data.PilotRepository
import com.example.pilot.data.model.Note
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = PilotRepository(application)

    val allNotes: StateFlow<List<Note>> = repo.notes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addNote(title: String, content: String = "", color: Int = 0xFFFFF9C4.toInt()) {
        viewModelScope.launch {
            repo.addNote(Note(title = title, content = content, color = color))
        }
    }

    fun togglePin(note: Note) {
        viewModelScope.launch {
            repo.updateNote(note.copy(isPinned = !note.isPinned, updatedAt = System.currentTimeMillis()))
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repo.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repo.deleteNote(note)
        }
    }
}
