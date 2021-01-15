package com.example.howie.ui

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.example.howie.R
import com.example.howie.core.ScheduleInXTimeUnits
import com.example.howie.core.TimeUnit
import com.example.howie.databinding.ViewScheduleInXTimeUnitsBinding

class ScheduleInXTimeUnitsView(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    private val binding =
        ViewScheduleInXTimeUnitsBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_schedule_in_x_time_units, this)
        ArrayAdapter.createFromResource(
            context,
            R.array.time_units,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.timeUnitSpinner.adapter = adapter
        }
    }

    fun setSchedule(schedule: ScheduleInXTimeUnits) {
        binding.quantityField.setText(schedule.quantity.toString())
        binding.timeUnitSpinner.setSelection(schedule.timeUnit.ordinal)
    }

    fun getSchedule() = ScheduleInXTimeUnits(
        convertTextToValidQuantity(binding.quantityField.text),
        TimeUnit.values()[binding.timeUnitSpinner.selectedItemPosition]
    )
}

private fun convertTextToValidQuantity(text: Editable): Long = try {
    text.toString().toLong().coerceAtLeast(1)
} catch (e: NumberFormatException) {
    1
}