package com.example.howie

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 0, entities = (arrayOf(Task::class)))
abstract class TasksDatabase : RoomDatabase() {
    abstract fun getTaskDao() : TaskDao
}

object TasksDatabaseSingleton {
    private var database: TasksDatabase? = null

    fun getDatabase(applicationContext: Context) :TasksDatabase {
        if (database == null) {
            database = Room.databaseBuilder(
                applicationContext,
                TasksDatabase::class.java, "tasks-database"
            ).build()
        }
        return database!!
    }
}