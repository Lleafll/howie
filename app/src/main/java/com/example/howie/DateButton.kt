package com.example.howie

import android.content.Context
import android.util.AttributeSet
import java.time.LocalDate

class DateButton(context: Context?, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatButton(context, attrs) {

    private var date: LocalDate = LocalDate.now();

    fun getDate(): LocalDate {
        return date
    }

    fun setDate(date: LocalDate) {
        this.date = date
        text = date.toString()
    }

}
