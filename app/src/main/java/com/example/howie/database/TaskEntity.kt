package com.example.howie

import androidx.room.*
import com.example.howie.core.Importance
import com.example.howie.core.Schedule
import com.example.howie.core.TimeUnit
import java.time.DayOfWeek
import java.time.LocalDate


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


@Entity(tableName = "Task")
@TypeConverters(Converters::class)
data class TaskEntity(
    val name: String,
    val taskListId: Long,
    val importance: Importance,
    val due: LocalDate?,
    val snoozed: LocalDate?,
    @Embedded(prefix = "schedule") val schedule: Schedule?,
    val completed: LocalDate?,
    val archived: LocalDate?,
    @PrimaryKey(autoGenerate = true)
    var id: Int
)
