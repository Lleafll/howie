package com.example.howie

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
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

@Entity
@TypeConverters(Converters::class)
data class Task(
    val name: String,
    val importance: Importance = Importance.IMPORTANT,
    val due: LocalDate? = null,
    val snoozed: LocalDate? = null,
    val completed: LocalDate? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}