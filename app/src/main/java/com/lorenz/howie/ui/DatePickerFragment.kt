package com.lorenz.howie.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.time.LocalDate
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    companion object {
        const val DATE_ID_ARGUMENT = "dateId"
        const val DATE_ARGUMENT = "date"
    }

    private lateinit var listener: DatePickerListener

    interface DatePickerListener {
        fun onDateChanged(id: Int, date: LocalDate)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateString = requireArguments().getString(DATE_ARGUMENT)
        val date = LocalDate.parse(dateString)
        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            this,
            date.year,
            date.monthValue - 1,
            date.dayOfMonth
        )
        datePickerDialog.datePicker.firstDayOfWeek = Calendar.MONDAY
        return datePickerDialog
    }

    override fun onDateSet(picker: DatePicker?, year: Int, month: Int, day: Int) {
        val id = requireArguments().getInt(DATE_ID_ARGUMENT, -1)
        listener.onDateChanged(id, LocalDate.of(year, month + 1, day))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DatePickerListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement DatePickerListener")
        }
    }
}