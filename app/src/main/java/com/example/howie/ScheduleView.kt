package com.example.howie

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.view_schedule.view.*

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
            schedule_spinner.adapter = adapter
        }
        schedule_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {
                hideAllViews()
            }

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                hideAllViews()
                when (position) {
                    0 -> scheduleInXTimeUnitsView.visibility = View.VISIBLE
                    1 -> scheduleForNextWeekDayView.visibility = View.VISIBLE
                    2 -> scheduleForNextDayOfMonthView.visibility = View.VISIBLE
                }
            }

            private fun hideAllViews() {
                scheduleInXTimeUnitsView.visibility = View.INVISIBLE
                scheduleForNextWeekDayView.visibility = View.INVISIBLE
                scheduleForNextDayOfMonthView.visibility = View.INVISIBLE
            }
        }
    }

    fun setSchedule(schedule: Schedule) = when {
        schedule.scheduleInXTimeUnits != null -> {
            schedule_spinner.setSelection(0)
            scheduleInXTimeUnitsView.setSchedule(schedule.scheduleInXTimeUnits!!)
        }
        schedule.scheduleForNextWeekDay != null -> {
            schedule_spinner.setSelection(1)
            scheduleForNextWeekDayView.setSchedule(schedule.scheduleForNextWeekDay!!)
        }
        schedule.scheduleForNextDayOfMonth != null -> {
            schedule_spinner.setSelection(2)
            scheduleForNextDayOfMonthView.setSchedule(schedule.scheduleForNextDayOfMonth!!)
        }
        else -> {
            error("Invalid schedule passed to ScheduleView.setSchedule()")
        }
    }

    fun getSchedule(): Schedule = when (schedule_spinner.selectedItemPosition) {
        0 -> Schedule(scheduleInXTimeUnitsView.getSchedule())
        1 -> Schedule(scheduleForNextWeekDayView.getSchedule())
        2 -> Schedule(scheduleForNextDayOfMonthView.getSchedule())
        else -> error("Schedule option item not implemented")
    }
}
