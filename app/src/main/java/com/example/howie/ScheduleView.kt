package com.example.howie

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.view_schedule.view.*
import kotlinx.android.synthetic.main.view_schedule_for_next_day_of_week.view.*

class ScheduleView(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_schedule, this)
        ArrayAdapter.createFromResource(
            context,
            R.array.schedule_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            day_of_week_spinner.adapter = adapter
        }
    }

    fun setSchedule(schedule: Schedule) = when {
        schedule.scheduleInXTimeUnits != null -> {
            day_of_week_spinner.setSelection(0)
            scheduleInXTimeUnitsView.setSchedule(schedule.scheduleInXTimeUnits!!)
        }
        schedule.scheduleForNextWeekDay != null -> {
            day_of_week_spinner.setSelection(1)
            scheduleForNextWeekDayView.setSchedule(schedule.scheduleForNextWeekDay!!)
        }
        schedule.scheduleForNextDayOfMonth != null -> {
            day_of_week_spinner.setSelection(2)
            scheduleForNextDayOfMonthView.setSchedule(schedule.scheduleForNextDayOfMonth!!)
        }
        else -> {
            error("Invalid schedule passed to ScheduleView.setSchedule()")
        }
    }

    fun getSchedule(): Schedule = when (day_of_week_spinner.selectedItemPosition) {
        0 -> Schedule(scheduleInXTimeUnitsView.getSchedule())
        1 -> Schedule(scheduleForNextWeekDayView.getSchedule())
        2 -> Schedule(scheduleForNextDayOfMonthView.getSchedule())
        else -> error("Schedule option item not implemented")
    }
}