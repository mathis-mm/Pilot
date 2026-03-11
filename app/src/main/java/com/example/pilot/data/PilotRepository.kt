package com.example.pilot.data

import android.content.Context
import com.example.pilot.data.model.*
import kotlinx.coroutines.flow.Flow

class PilotRepository(context: Context) {
    private val db = PilotDatabase.getInstance(context)
    private val taskDao = db.taskDao()
    private val eventDao = db.eventDao()
    private val noteDao = db.noteDao()
    private val habitDao = db.habitDao()
    private val reminderDao = db.reminderDao()

    // Tasks
    val tasks: Flow<List<Task>> = taskDao.getAll()
    suspend fun addTask(task: Task) = taskDao.insert(task)
    suspend fun updateTask(task: Task) = taskDao.update(task)
    suspend fun deleteTask(task: Task) = taskDao.delete(task)
    suspend fun deleteCompletedTasks() = taskDao.deleteCompleted()

    // Events
    val events: Flow<List<Event>> = eventDao.getAll()
    suspend fun addEvent(event: Event) = eventDao.insert(event)
    suspend fun updateEvent(event: Event) = eventDao.update(event)
    suspend fun deleteEvent(event: Event) = eventDao.delete(event)

    // Notes
    val notes: Flow<List<Note>> = noteDao.getAll()
    suspend fun addNote(note: Note) = noteDao.insert(note)
    suspend fun updateNote(note: Note) = noteDao.update(note)
    suspend fun deleteNote(note: Note) = noteDao.delete(note)

    // Habits
    val habits: Flow<List<Habit>> = habitDao.getAll()
    val habitEntries: Flow<List<HabitEntry>> = habitDao.getAllEntries()
    suspend fun addHabit(habit: Habit) = habitDao.insert(habit)
    suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteEntriesForHabit(habit.id)
        habitDao.delete(habit)
    }
    suspend fun addHabitEntry(entry: HabitEntry) = habitDao.insertEntry(entry)
    suspend fun deleteHabitEntry(habitId: Long, date: Long) = habitDao.deleteEntry(habitId, date)

    // Reminders
    val reminders: Flow<List<Reminder>> = reminderDao.getAll()
    suspend fun addReminder(reminder: Reminder): Long = reminderDao.insert(reminder)
    suspend fun deleteReminder(reminderId: Long) = reminderDao.deleteById(reminderId)
    suspend fun getRemindersForReference(type: ReminderType, referenceId: Long) = reminderDao.getForReference(type, referenceId)
}
