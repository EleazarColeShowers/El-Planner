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
    suspend fun insertTask(task: com.example.elplanner.data.TaskItem)

    // Filter tasks by userId
    @Query("SELECT * FROM tasks WHERE userId = :userId")
    fun getUserTasks(userId: String): Flow<List<TaskItem>>

    @Delete
    suspend fun deleteTask(task: TaskItem)
}
