package com.example.howie

import android.content.Context
import android.util.AttributeSet
import java.util.*

class DateButton(context: Context, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatButton(context, attrs) {

    private var date: Calendar = Calendar.getInstance();

    fun getDate(): Calendar {
        return date
    }

    fun setDate(date: Calendar) {
        this.date = date
        text = toDateString(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
    }

}

private fun toDateString(year: Int, month: Int, day: Int): String {
    return "$day.$month.$year"
}
