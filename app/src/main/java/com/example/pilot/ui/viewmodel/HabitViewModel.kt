package com.example.pilot.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pilot.data.PilotRepository
import com.example.pilot.data.model.Habit
import com.example.pilot.data.model.HabitEntry
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class HabitViewModel : ViewModel() {
    private val repo = PilotRepository

    val allHabits: StateFlow<List<Habit>> = repo.habits
    val allEntries: StateFlow<List<HabitEntry>> = repo.habitEntries

    fun addHabit(name: String, icon: String = "✅") {
        repo.addHabit(Habit(name = name, icon = icon))
    }

    fun toggleHabitForToday(habit: Habit, entries: List<HabitEntry>) {
        val today = getTodayStart()
        val existing = entries.find { it.habitId == habit.id && it.date == today }
        if (existing != null) {
            repo.deleteHabitEntry(habit.id, today)
        } else {
            repo.addHabitEntry(HabitEntry(habitId = habit.id, date = today))
        }
    }

    fun deleteHabit(habit: Habit) {
        repo.deleteHabit(habit)
    }

    companion object {
        fun getTodayStart(): Long {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }
    }
}
