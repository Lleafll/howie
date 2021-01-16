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

    private val _binding =
        ViewScheduleInXTimeUnitsBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        ArrayAdapter.createFromResource(
            context,
            R.array.time_units,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            _binding.timeUnitSpinner.adapter = adapter
        }
    }

    fun setSchedule(schedule: ScheduleInXTimeUnits) {
        _binding.quantityField.setText(schedule.quantity.toString())
        _binding.timeUnitSpinner.setSelection(schedule.timeUnit.ordinal)
    }

    fun getSchedule() = ScheduleInXTimeUnits(
        convertTextToValidQuantity(_binding.quantityField.text),
        TimeUnit.values()[_binding.timeUnitSpinner.selectedItemPosition]
    )
}

private fun convertTextToValidQuantity(text: Editable): Long = try {
    text.toString().toLong().coerceAtLeast(1)
} catch (e: NumberFormatException) {
    1
}