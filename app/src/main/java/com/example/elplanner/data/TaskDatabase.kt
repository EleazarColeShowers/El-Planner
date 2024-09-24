package com.example.elplanner.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TaskItem::class], version = 3, exportSchema = false)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao
    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN category TEXT DEFAULT 'Uncategorized'")
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN userId TEXT NOT NULL DEFAULT 'Uncategorized'")
            }
        }
        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // Apply the migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
