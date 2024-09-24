package com.example.elplanner.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class TaskRepository(private val taskDao: TaskDao) {

    suspend fun insertTask(taskItem: TaskItem) {
        taskDao.insertTask(taskItem)
    }

    suspend fun deleteTask(taskItem: TaskItem) {
        taskDao.deleteTask(taskItem)
    }

    // Add method to get tasks for the current user
    fun getUserTasks(userId: String): Flow<List<TaskItem>> {
        return taskDao.getUserTasks(userId)
    }
}