package com.example.howie.core

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.example.howie.R
import kotlinx.android.synthetic.main.view_schedule_for_next_day_of_week.view.*
import java.time.DayOfWeek

class ScheduleForNextWeekDayView(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_schedule_for_next_day_of_week, this)
        ArrayAdapter.createFromResource(
            context,
            R.array.week_days,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            day_of_week_spinner.adapter = adapter
        }
    }

    fun setSchedule(schedule: ScheduleForNextWeekDay) {
        day_of_week_spinner.setSelection(schedule.weekDay.ordinal)
    }

    fun getSchedule() = ScheduleForNextWeekDay(
        DayOfWeek.values()[day_of_week_spinner.selectedItemPosition]
    )
}