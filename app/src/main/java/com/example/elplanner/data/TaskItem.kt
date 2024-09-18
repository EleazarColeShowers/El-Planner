package com.example.elplanner.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // This is the primary key
    val task: String,
    val description: String?,
    val date: String,
    val time: String,
    val priorityFlag: Int?
)
