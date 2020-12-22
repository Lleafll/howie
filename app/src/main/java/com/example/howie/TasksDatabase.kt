package com.example.howie

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(version = 9, entities = (arrayOf(Task::class, TaskList::class)))
abstract class TasksDatabase : RoomDatabase() {
    abstract fun getTaskDao(): TaskDao
    abstract fun getTaskListDao(): TaskListDao
}

object TasksDatabaseSingleton {
    private var database: TasksDatabase? = null

    fun getDatabase(applicationContext: Context): TasksDatabase {
        if (database == null) {
            database = Room.databaseBuilder(
                applicationContext,
                TasksDatabase::class.java,
                "tasks-database"
            ).addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
                .addCallback(atLeastOneTaskListCallback)
                .build()
        }
        return database!!
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Task ADD archived INTEGER")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Task ADD taskListId INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS TaskList (id INTEGER PRIMARY KEY, name STRING NOT NULL)")
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE TaskList")
        database.execSQL("CREATE TABLE IF NOT EXISTS TaskList (id INTEGER PRIMARY KEY, name TEXT NOT NULL)")
    }
}

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE TaskList")
        database.execSQL("CREATE TABLE TaskList (id INTEGER NOT NULL DEFAULT 0, name TEXT NOT NULL, PRIMARY KEY(id))")
    }
}

private val atLeastOneTaskListCallback = object : RoomDatabase.Callback() {
    override fun onCreate(database: SupportSQLiteDatabase) {
        super.onCreate(database)
        database.execSQL("INSERT OR IGNORE INTO TaskList (id, name) VALUES (0, 'Tasks')")
    }
}