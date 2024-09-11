package com.example.elplanner.data

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

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
    private val _taskList = mutableStateListOf<TaskItem>()
    val taskList: List<TaskItem> get() = _taskList

    fun addTask(task: String, description: String?, date: String, time: String, priorityFlag: Int?) {
        Log.d("TaskViewModel", "Adding task: $task, Description: $description, Date: $date, Time: $time")
        _taskList.add(TaskItem(task, description, date, time, priorityFlag))
        Log.d("TaskViewModel", "Task List Size: ${_taskList.size}")
    }
}