package com.example.pilot.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pilot.data.dao.*
import com.example.pilot.data.model.*

@Database(
    entities = [Task::class, Event::class, Note::class, Habit::class, HabitEntry::class, Reminder::class],
    version = 1,
    exportSchema = false
)
abstract class PilotDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun eventDao(): EventDao
    abstract fun noteDao(): NoteDao
    abstract fun habitDao(): HabitDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: PilotDatabase? = null

        fun getInstance(context: Context): PilotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PilotDatabase::class.java,
                    "pilot_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
