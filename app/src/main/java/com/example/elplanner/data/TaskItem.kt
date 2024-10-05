package com.example.elplanner.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val task: String,
    val description: String?,
    val date: String,
    val time: String,
    val priorityFlag: Int?,
    var category: String? = null,
    val userId: String,
    var completed: Boolean = false
)
