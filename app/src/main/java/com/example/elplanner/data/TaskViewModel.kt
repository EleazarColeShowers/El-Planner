package com.example.elplanner.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TaskViewModel : ViewModel() {
    var task by mutableStateOf("")
    var description by mutableStateOf("")
    var selectedDate by mutableStateOf("")
    var selectedTime by mutableStateOf("")

    fun getDateTime(): String {
        return "$selectedDate $selectedTime"
    }
}