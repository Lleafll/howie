package com.example.howie

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.time.LocalDate
import java.util.*

class DatePickerFragment(private val listener: (LocalDate) -> Unit) : DialogFragment(),
    DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = LocalDate.now()
        return DatePickerDialog(activity!!, this, date.year, date.monthValue - 1, date.dayOfMonth)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        listener(LocalDate.of(year, month + 1, day))
    }
}