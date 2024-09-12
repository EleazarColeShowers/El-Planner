package com.example.elplanner.data

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class TaskItem(
    val task: String,
    val description: String?,
    val date: String,
    val time: String,
    val priorityFlag: Int?
)

class TaskViewModel : ViewModel() {
    var task by mutableStateOf("")
    var description by mutableStateOf("")
    var selectedDate by mutableStateOf("")
    var selectedTime by mutableStateOf("")
    var selectedPriority by mutableStateOf(-1)

    fun getDateTime(): String {
        return "$selectedDate $selectedTime"
    }
    private val _taskList = MutableStateFlow<List<TaskItem>>(emptyList())
    val taskList: StateFlow<List<TaskItem>> = _taskList

    fun addTask(task: String, description: String?, date: String, time: String, priorityFlag: Int?) {
        viewModelScope.launch {
            Log.d("TaskViewModel", "Adding task: $task, Description: $description, Date: $date, Time: $time")
            val newTaskItem = TaskItem(task, description, date, time, priorityFlag)
            _taskList.value = _taskList.value.toMutableList().apply { add(newTaskItem) }
            Log.d("TaskViewModel", "Task List Size after update: ${_taskList.value.size}")
        }
    }
}