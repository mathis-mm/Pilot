package com.example.pilot.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pilot.data.PilotRepository
import com.example.pilot.data.model.Task
import com.example.pilot.data.model.TaskPriority
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = PilotRepository(application)

    val allTasks: StateFlow<List<Task>> = repo.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTask(title: String, description: String = "", priority: TaskPriority = TaskPriority.MEDIUM, dueDate: Long? = null) {
        viewModelScope.launch {
            repo.addTask(Task(title = title, description = description, priority = priority, dueDate = dueDate))
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            repo.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repo.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repo.deleteTask(task)
        }
    }

    fun deleteCompletedTasks() {
        viewModelScope.launch {
            repo.deleteCompletedTasks()
        }
    }
}
