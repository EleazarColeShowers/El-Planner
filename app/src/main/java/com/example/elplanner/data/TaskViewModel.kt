package com.example.elplanner.data

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class TaskViewModel(
    application: Application,
    private val repository: TaskRepository,
    private val firebaseUserRepository: FirebaseUserRepository
) : AndroidViewModel(application) {
    private val taskDao: TaskDao = TaskDatabase.getDatabase(application).taskDao()

    var task by mutableStateOf("")
    var description by mutableStateOf("")
    var selectedPriority by mutableStateOf(-1)

    val taskList = MutableStateFlow<List<TaskItem>>(emptyList())

//    fun loadUserTasks(userId: String) {
//        viewModelScope.launch {
//            repository.getUserTasks(userId).collect { tasks ->
//                taskList.value = tasks
//            }
//        }
//    }

    fun loadUserTasks(userId: String) {
        val userId = firebaseUserRepository.getCurrentUserId()
        if (userId != null) {
            viewModelScope.launch {
                repository.getUserTasks(userId).collect { tasks ->
                    taskList.value = tasks
                }
            }
        }
    }


    fun addTask(task: String, description: String, date: String, time: String, priorityFlag: Int?, category: String?, userId: String) {
        viewModelScope.launch {
            val taskEntity = TaskItem(
                task = task,
                description = description,
                date = date,
                time = time,
                priorityFlag = priorityFlag ?: 0,
                category = category,
                userId = userId
            )
            repository.insertTask(taskEntity)
        }
    }

    fun updateTask(updatedTaskItem: TaskItem) {
        viewModelScope.launch {
            repository.insertTask(updatedTaskItem)
        }
    }

    fun deleteTask(taskItem: TaskItem) {
        viewModelScope.launch {
            repository.deleteTask(taskItem)
        }
    }

    fun markTaskAsCompleted(taskItem: TaskItem, isCompleted: Boolean) {
        val updatedTask = taskItem.copy(completed = isCompleted)
        viewModelScope.launch {
            repository.insertTask(updatedTask)
        }
    }
}

class TaskViewModelFactory(
    private val application: Application,
    private val taskRepository: TaskRepository,
    private val firebaseUserRepository: FirebaseUserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(application, taskRepository, firebaseUserRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}