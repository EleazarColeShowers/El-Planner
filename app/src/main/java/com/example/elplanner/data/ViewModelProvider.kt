package com.example.elplanner.data

import android.app.Application
import android.content.Context

object ViewModelProvider {
    private var taskViewModel: TaskViewModel? = null

    fun getTaskViewModel(context: Context): TaskViewModel {
        if (taskViewModel == null) {
            val application = context.applicationContext as Application
            val taskDao = TaskDatabase.getDatabase(application).taskDao()
            val repository = TaskRepository(taskDao)
            val firebaseUserRepository= FirebaseUserRepository()
            taskViewModel = TaskViewModel(application, repository, firebaseUserRepository)
        }
        return taskViewModel!!
    }
}