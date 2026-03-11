package com.example.pilot.data.dao

import androidx.room.*
import com.example.pilot.data.model.Reminder
import com.example.pilot.data.model.ReminderType
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY triggerTimeMillis ASC")
    fun getAll(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE type = :type AND referenceId = :referenceId")
    suspend fun getForReference(type: ReminderType, referenceId: Long): List<Reminder>

    @Insert
    suspend fun insert(reminder: Reminder): Long

    @Delete
    suspend fun delete(reminder: Reminder)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteById(id: Long)
}
