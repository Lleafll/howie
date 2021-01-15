package com.example.howie.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.howie.R
import com.example.howie.core.Schedule
import com.example.howie.databinding.ViewScheduleBinding

class ScheduleView(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private val binding = ViewScheduleBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        ArrayAdapter.createFromResource(
            context,
            R.array.schedule_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.scheduleSpinner.adapter = adapter
        }
        binding.scheduleSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
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
                        0 -> binding.scheduleInXTimeUnitsView.visibility = View.VISIBLE
                        1 -> binding.scheduleForNextWeekDayView.visibility = View.VISIBLE
                        2 -> binding.scheduleForNextDayOfMonthView.visibility = View.VISIBLE
                    }
                }

                private fun hideAllViews() {
                    binding.scheduleInXTimeUnitsView.visibility = View.INVISIBLE
                    binding.scheduleForNextWeekDayView.visibility = View.INVISIBLE
                    binding.scheduleForNextDayOfMonthView.visibility = View.INVISIBLE
                }
            }
    }

    fun setSchedule(schedule: Schedule) = when {
        schedule.scheduleInXTimeUnits != null -> {
            binding.scheduleSpinner.setSelection(0)
            binding.scheduleInXTimeUnitsView.setSchedule(schedule.scheduleInXTimeUnits!!)
        }
        schedule.scheduleForNextWeekDay != null -> {
            binding.scheduleSpinner.setSelection(1)
            binding.scheduleForNextWeekDayView.setSchedule(schedule.scheduleForNextWeekDay!!)
        }
        schedule.scheduleForNextDayOfMonth != null -> {
            binding.scheduleSpinner.setSelection(2)
            binding.scheduleForNextDayOfMonthView.setSchedule(schedule.scheduleForNextDayOfMonth!!)
        }
        else -> {
            error("Invalid schedule passed to ScheduleView.setSchedule()")
        }
    }

    fun getSchedule(): Schedule = when (binding.scheduleSpinner.selectedItemPosition) {
        0 -> Schedule(binding.scheduleInXTimeUnitsView.getSchedule())
        1 -> Schedule(binding.scheduleForNextWeekDayView.getSchedule())
        2 -> Schedule(binding.scheduleForNextDayOfMonthView.getSchedule())
        else -> error("Schedule option item not implemented")
    }
}
