package com.example.elplanner.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao { 
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Delete
    suspend fun deleteTask(task: TaskItem)
}