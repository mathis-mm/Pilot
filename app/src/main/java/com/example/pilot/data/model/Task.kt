package com.example.pilot.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskPriority { LOW, MEDIUM, HIGH }

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
