package com.example.pilot.data.model

enum class TaskPriority { LOW, MEDIUM, HIGH }

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
