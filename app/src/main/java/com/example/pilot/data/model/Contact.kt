package com.example.pilot.data.model

data class Contact(
    val id: Long = 0,
    val name: String,
    val phone: String = "",
    val email: String = "",
    val note: String = "",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
