package com.example.howie

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.time.DayOfWeek
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

    @TypeConverter
    fun toTimeUnit(index: Int) = TimeUnit.values()[index]

    @TypeConverter
    fun toInt(timeUnit: TimeUnit) = timeUnit.ordinal

    @TypeConverter
    fun toDayOfWeek(index: Int) = DayOfWeek.values()[index]

    @TypeConverter
    fun toInt(dayOfWeek: DayOfWeek) = dayOfWeek.ordinal
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
    @Embedded(prefix = "schedule") val schedule: Schedule? = null,
    val completed: LocalDate? = null,
    val archived: LocalDate? = null
) : Parcelable {
    @IgnoredOnParcel
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