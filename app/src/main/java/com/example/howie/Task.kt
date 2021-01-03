package com.example.howie

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

enum class Importance {
    IMPORTANT, UNIMPORTANT
}

class Converters {
    @TypeConverter
    fun toImportance(index: Int): Importance = Importance.values()[index]

    @TypeConverter
    fun toInt(importance: Importance): Int = importance.ordinal

    @TypeConverter
    fun toLocalDate(epochDay: Long?): LocalDate? =
        if (epochDay == null) {
            null
        } else {
            LocalDate.ofEpochDay(epochDay)
        }

    @TypeConverter
    fun toLong(date: LocalDate?): Long? = date?.toEpochDay()
}

@Parcelize
@Entity
@TypeConverters(Converters::class)
data class Task(
    val name: String,
    val taskListId: Long,
    val importance: Importance = Importance.IMPORTANT,
    val due: LocalDate? = null,
    val snoozed: LocalDate? = null,
    val completed: LocalDate? = null,
    val archived: LocalDate? = null
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

enum class TaskCategory {
    DO, DECIDE, DELEGATE, DROP
}

fun taskCategory(task: Task): TaskCategory = if (task.importance == Importance.IMPORTANT) {
    if (task.due != null) {
        TaskCategory.DO
    } else {
        TaskCategory.DECIDE
    }
} else {
    if (task.due != null) {
        TaskCategory.DELEGATE
    } else {
        TaskCategory.DROP
    }
}