package com.example.elplanner.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class TaskRepository(private val taskDao: TaskDao) {

    // A function to insert a task into the database
    suspend fun insertTask(taskItem: TaskItem) {
        taskDao.insertTask(taskItem)
    }

    // A function to get all tasks from the database
    fun getAllTasks(): Flow<List<TaskItem>> {
        return taskDao.getAllTasks()
    }

    // Add more functions as needed for interacting with your database
}
