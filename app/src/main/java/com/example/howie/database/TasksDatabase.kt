package com.example.howie.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.howie.TaskEntity

@Database(
    version = 11,
    entities = (arrayOf(TaskEntity::class, TaskListEntity::class, WidgetSettings::class))
)
abstract class TasksDatabase : RoomDatabase() {
    abstract fun getTaskDao(): TaskDao
    abstract fun getTaskListDao(): TaskListDao
    abstract fun getWidgetSettingsDao(): WidgetSettingsDao
}

private lateinit var INSTANCE: TasksDatabase

fun getDatabase(applicationContext: Context): TasksDatabase {
    synchronized(TasksDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                applicationContext,
                TasksDatabase::class.java,
                "tasks-database"
            ).addMigrations(
                MIGRATION_4_5,
                MIGRATION_5_6,
                MIGRATION_6_7,
                MIGRATION_7_8,
                MIGRATION_8_9,
                MIGRATION_9_10,
                MIGRATION_10_11
            ).addCallback(atLeastOneTaskListCallback)
                .build()
        }
    }
    return INSTANCE
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

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE WidgetSettings (widgetId INTEGER NOT NULL DEFAULT 0, taskListId INTEGER NOT NULL, PRIMARY KEY(widgetId))")
    }
}

val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        addScheduleInXTimeUnits(database)
        addForNextWeekDay(database)
        addForNextDayOfMonth(database)
    }

    private fun addScheduleInXTimeUnits(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Task ADD scheduleinXquantity INTEGER")
        database.execSQL("ALTER TABLE Task ADD scheduleinXtimeUnit INTEGER")
    }

    private fun addForNextWeekDay(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Task ADD scheduleforNextWeekDayweekDay INTEGER")
    }

    private fun addForNextDayOfMonth(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Task ADD scheduleforNextDayOfMonthdayOfMonth INTEGER")
    }
}

private val atLeastOneTaskListCallback = object : RoomDatabase.Callback() {
    override fun onOpen(database: SupportSQLiteDatabase) {
        super.onOpen(database)
        database.execSQL("INSERT OR IGNORE INTO TaskList (id, name) VALUES (0, 'Tasks')")
    }
}
