package com.example.pilot.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String = "✅",
    val targetDays: Int = 7,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habit_entries")
data class HabitEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val date: Long,
    val completed: Boolean = true
)
