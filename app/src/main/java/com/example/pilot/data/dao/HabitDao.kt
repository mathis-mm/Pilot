package com.example.pilot.data.dao

import androidx.room.*
import com.example.pilot.data.model.Habit
import com.example.pilot.data.model.HabitEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Habit>>

    @Insert
    suspend fun insert(habit: Habit): Long

    @Delete
    suspend fun delete(habit: Habit)

    @Query("SELECT * FROM habit_entries")
    fun getAllEntries(): Flow<List<HabitEntry>>

    @Insert
    suspend fun insertEntry(entry: HabitEntry): Long

    @Query("DELETE FROM habit_entries WHERE habitId = :habitId AND date = :date")
    suspend fun deleteEntry(habitId: Long, date: Long)

    @Query("DELETE FROM habit_entries WHERE habitId = :habitId")
    suspend fun deleteEntriesForHabit(habitId: Long)
}
