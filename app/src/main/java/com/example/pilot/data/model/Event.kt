package com.example.pilot.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val startTime: Long,
    val endTime: Long,
    val color: Int = 0xFF6C63FF.toInt(),
    val createdAt: Long = System.currentTimeMillis()
)
