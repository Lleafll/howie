package com.example.howie

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.task_item.view.*
import java.time.LocalDate

class TaskItem : LinearLayout {
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)

    init {
        inflate(context, R.layout.task_item, this)
    }

    var task: Task = Task("", 0)
        set(value) {
            field = value
            name_text_view.text = value.name
            due_text_view.text = toDateString("", value.due)
            snoozed_text_view.text = toDateString("\u23F0 ", value.snoozed)
            archived_text_view.text = toDateString("\uD83D\uDDC3 ", value.archived)
        }
}

private fun toDateString(prefix: String, date: LocalDate?): String {
    return if (date == null) {
        ""
    } else {
        prefix + date.toString()
    }
}