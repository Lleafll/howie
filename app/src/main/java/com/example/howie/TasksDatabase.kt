package com.example.howie

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(version = 6, entities = (arrayOf(Task::class)))
abstract class TasksDatabase : RoomDatabase() {
    abstract fun getTaskDao(): TaskDao
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Task ADD archived INTEGER")
    }
}

val MIGRATION_5_6 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Task ADD taskListId INTEGER NOT NULL DEFAULT 0")
    }
}

object TasksDatabaseSingleton {
    private var database: TasksDatabase? = null

    fun getDatabase(applicationContext: Context): TasksDatabase {
        if (database == null) {
            database = Room.databaseBuilder(
                applicationContext,
                TasksDatabase::class.java,
                "tasks-database"
            ).addMigrations(MIGRATION_4_5, MIGRATION_5_6).build()
        }
        return database!!
    }
}