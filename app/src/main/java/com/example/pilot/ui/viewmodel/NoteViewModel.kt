package com.example.pilot.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pilot.data.PilotRepository
import com.example.pilot.data.model.Note
import kotlinx.coroutines.flow.StateFlow

class NoteViewModel : ViewModel() {
    private val repo = PilotRepository

    val allNotes: StateFlow<List<Note>> = repo.notes

    fun addNote(title: String, content: String = "", color: Int = 0xFFFFF9C4.toInt()) {
        repo.addNote(Note(title = title, content = content, color = color))
    }

    fun togglePin(note: Note) {
        repo.updateNote(note.copy(isPinned = !note.isPinned, updatedAt = System.currentTimeMillis()))
    }

    fun updateNote(note: Note) {
        repo.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
    }

    fun deleteNote(note: Note) {
        repo.deleteNote(note)
    }
}
