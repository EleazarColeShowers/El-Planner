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
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
//val userId = firebaseUserRepository.getCurrentUserId()

    fun loadUserTasks(userId: String) {
        viewModelScope.launch {
            // First, fetch tasks from the Room database
            repository.getUserTasks(userId).collect { tasks ->
                taskList.value = tasks
            }

            // Then, fetch tasks from Firebase Realtime Database
            val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/tasks")
            databaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val firebaseTaskList = mutableListOf<TaskItem>()
                    for (taskSnapshot in snapshot.children) {
                        val task = taskSnapshot.getValue(TaskItem::class.java)
                        task?.let { firebaseTaskList.add(it) }
                    }
                    // Combine Room tasks with Firebase tasks and update the task list
                    taskList.value += firebaseTaskList
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Failed to load tasks from Firebase: ${error.message}")
                }
            })
        }
    }

    fun syncRoomTasksToFirebase() {
        viewModelScope.launch {
            val userId = firebaseUserRepository.getCurrentUserId()
            if (userId != null) {
                // Fetch tasks from Room
                repository.getUserTasks(userId).collect { taskList ->
                    taskList.forEach { taskItem ->
                        firebaseUserRepository.syncTaskToFirebase(taskItem, {
                            Log.d("FirebaseSync", "Task ${taskItem.id} synced successfully")
                        }, { exception ->
                            Log.e("FirebaseSync", "Error syncing task ${taskItem.id}", exception)
                        })
                    }
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