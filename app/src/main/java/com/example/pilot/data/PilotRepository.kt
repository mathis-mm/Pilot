package com.example.pilot.data

import com.example.pilot.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PilotRepository {
    private var nextTaskId = 1L
    private var nextEventId = 1L
    private var nextContactId = 1L
    private var nextNoteId = 1L
    private var nextHabitId = 1L
    private var nextEntryId = 1L
    private var nextReminderId = 1L

    // Tasks
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    fun addTask(task: Task) {
        _tasks.value = _tasks.value + task.copy(id = nextTaskId++)
    }

    fun updateTask(task: Task) {
        _tasks.value = _tasks.value.map { if (it.id == task.id) task else it }
    }

    fun deleteTask(task: Task) {
        _tasks.value = _tasks.value.filter { it.id != task.id }
    }

    // Events
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    fun addEvent(event: Event) {
        _events.value = _events.value + event.copy(id = nextEventId++)
    }

    fun updateEvent(event: Event) {
        _events.value = _events.value.map { if (it.id == event.id) event else it }
    }

    fun deleteEvent(event: Event) {
        _events.value = _events.value.filter { it.id != event.id }
    }

    // Contacts
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    fun addContact(contact: Contact) {
        _contacts.value = _contacts.value + contact.copy(id = nextContactId++)
    }

    fun updateContact(contact: Contact) {
        _contacts.value = _contacts.value.map { if (it.id == contact.id) contact else it }
    }

    fun deleteContact(contact: Contact) {
        _contacts.value = _contacts.value.filter { it.id != contact.id }
    }

    // Notes
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    fun addNote(note: Note) {
        _notes.value = _notes.value + note.copy(id = nextNoteId++)
    }

    fun updateNote(note: Note) {
        _notes.value = _notes.value.map { if (it.id == note.id) note else it }
    }

    fun deleteNote(note: Note) {
        _notes.value = _notes.value.filter { it.id != note.id }
    }

    // Habits
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private val _habitEntries = MutableStateFlow<List<HabitEntry>>(emptyList())
    val habitEntries: StateFlow<List<HabitEntry>> = _habitEntries.asStateFlow()

    fun addHabit(habit: Habit) {
        _habits.value = _habits.value + habit.copy(id = nextHabitId++)
    }

    fun deleteHabit(habit: Habit) {
        _habits.value = _habits.value.filter { it.id != habit.id }
        _habitEntries.value = _habitEntries.value.filter { it.habitId != habit.id }
    }

    fun addHabitEntry(entry: HabitEntry) {
        _habitEntries.value = _habitEntries.value + entry.copy(id = nextEntryId++)
    }

    fun deleteHabitEntry(habitId: Long, date: Long) {
        _habitEntries.value = _habitEntries.value.filter {
            !(it.habitId == habitId && it.date == date)
        }
    }

    // Reminders
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    fun addReminder(reminder: Reminder): Long {
        val id = nextReminderId++
        _reminders.value = _reminders.value + reminder.copy(id = id)
        return id
    }

    fun deleteReminder(reminderId: Long) {
        _reminders.value = _reminders.value.filter { it.id != reminderId }
    }

    fun getRemindersForReference(type: ReminderType, referenceId: Long): List<Reminder> {
        return _reminders.value.filter { it.type == type && it.referenceId == referenceId }
    }
}
