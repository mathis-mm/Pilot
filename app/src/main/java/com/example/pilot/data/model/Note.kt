package com.example.pilot.data.model

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String = "",
    val color: Int = 0xFFFFF9C4.toInt(),
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
