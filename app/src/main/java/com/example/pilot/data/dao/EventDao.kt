package com.example.pilot.data.dao

import androidx.room.*
import com.example.pilot.data.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY startTime ASC")
    fun getAll(): Flow<List<Event>>

    @Insert
    suspend fun insert(event: Event): Long

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)
}
