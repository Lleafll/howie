package com.lorenz.howie.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.lorenz.howie.R
import com.lorenz.howie.core.ScheduleForNextWeekDay
import com.lorenz.howie.databinding.ViewScheduleForNextDayOfWeekBinding
import java.time.DayOfWeek

class ScheduleForNextWeekDayView(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    private val binding =
        ViewScheduleForNextDayOfWeekBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        ArrayAdapter.createFromResource(
            context,
            R.array.week_days,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.dayOfWeekSpinner.adapter = adapter
        }
    }

    fun setSchedule(schedule: ScheduleForNextWeekDay) {
        binding.dayOfWeekSpinner.setSelection(schedule.weekDay.ordinal)
    }

    fun getSchedule() = ScheduleForNextWeekDay(
        DayOfWeek.values()[binding.dayOfWeekSpinner.selectedItemPosition]
    )
}