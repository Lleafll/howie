package com.example.howie

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.isVisible
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
            if (task.archived != null) {
                reschedule_button.isVisible = false
                archive_button.isVisible = false
                unarchive_button.isVisible = true
            } else if (value.schedule != null) {
                reschedule_button.isVisible = true
                archive_button.isVisible = false
                unarchive_button.isVisible = false
            } else {
                reschedule_button.isVisible = false
                archive_button.isVisible = true
                unarchive_button.isVisible = false
            }
        }

    fun toggle() = if (bottom_layout.isVisible) {
        collapse()
    } else {
        expand()
    }

    fun collapse() {
        bottom_layout.isVisible = false
    }

    fun expand() {
        bottom_layout.isVisible = true
    }
}

private fun toDateString(prefix: String, date: LocalDate?): String {
    return if (date == null) {
        ""
    } else {
        prefix + date.toString()
    }
}