package com.example.pilot.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pilot.data.PilotRepository
import com.example.pilot.data.model.Habit
import com.example.pilot.data.model.HabitEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*

class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = PilotRepository(application)

    val allHabits: StateFlow<List<Habit>> = repo.habits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEntries: StateFlow<List<HabitEntry>> = repo.habitEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addHabit(name: String, icon: String = "✅") {
        viewModelScope.launch {
            repo.addHabit(Habit(name = name, icon = icon))
        }
    }

    fun toggleHabitForToday(habit: Habit, entries: List<HabitEntry>) {
        val today = getTodayStart()
        val existing = entries.find { it.habitId == habit.id && it.date == today }
        viewModelScope.launch {
            if (existing != null) {
                repo.deleteHabitEntry(habit.id, today)
            } else {
                repo.addHabitEntry(HabitEntry(habitId = habit.id, date = today))
            }
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repo.deleteHabit(habit)
        }
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
