package com.example.pilot.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pilot.data.PilotRepository
import com.example.pilot.data.model.Task
import com.example.pilot.data.model.TaskPriority
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow

class TaskViewModel : ViewModel() {
    private val repo = PilotRepository

    val allTasks: StateFlow<List<Task>> = repo.tasks

    val activeTasks: StateFlow<List<Task>> get() = repo.tasks

    val activeTaskCount: StateFlow<List<Task>> get() = repo.tasks

    fun addTask(title: String, description: String = "", priority: TaskPriority = TaskPriority.MEDIUM, dueDate: Long? = null) {
        repo.addTask(Task(title = title, description = description, priority = priority, dueDate = dueDate))
    }

    fun toggleTask(task: Task) {
        repo.updateTask(task.copy(isCompleted = !task.isCompleted))
    }

    fun updateTask(task: Task) {
        repo.updateTask(task)
    }

    fun deleteTask(task: Task) {
        repo.deleteTask(task)
    }
}
