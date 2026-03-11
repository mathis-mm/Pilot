package com.example.pilot.data.model

data class Habit(
    val id: Long = 0,
    val name: String,
    val icon: String = "✅",
    val targetDays: Int = 7,
    val createdAt: Long = System.currentTimeMillis()
)

data class HabitEntry(
    val id: Long = 0,
    val habitId: Long,
    val date: Long,
    val completed: Boolean = true
)
