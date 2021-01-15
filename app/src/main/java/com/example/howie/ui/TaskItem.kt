package com.example.howie.ui

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.howie.R
import kotlinx.android.synthetic.main.task_item.view.*

class TaskItem : LinearLayout {
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)

    interface Listener {
        fun onSnoozeToTomorrowClicked()
        fun onRemoveSnoozeClicked()
        fun onRescheduleClicked()
        fun onArchiveClicked()
        fun onUnarchiveClicked()
        fun onEditClicked()
        fun onSelected()
    }

    init {
        inflate(context, R.layout.task_item, this)
        snooze_to_tomorrow.setOnClickListener {
            _listener?.onSnoozeToTomorrowClicked()
        }
        remove_snooze.setOnClickListener {
            _listener?.onRemoveSnoozeClicked()
        }
        reschedule_button.setOnClickListener {
            _listener?.onRescheduleClicked()
        }
        archive_button.setOnClickListener {
            _listener?.onArchiveClicked()
        }
        unarchive_button.setOnClickListener {
            _listener?.onUnarchiveClicked()
        }
        edit_button.setOnClickListener {
            _listener?.onEditClicked()
        }
        setOnClickListener {
            _listener?.onSelected()
        }
    }

    private var _listener: Listener? = null

    fun setFields(fields: TaskItemFields) {
        name_text_view.text = fields.name
        setDateString(due_text_view, fields.due)
        setDateString(snoozed_text_view, fields.snoozed)
        setDateString(archived_text_view, fields.archived)
        snooze_to_tomorrow.isVisible = fields.snoozedToTomorrow
        remove_snooze.isVisible = fields.removeSnoozed
        reschedule_button.isVisible = fields.reschedule != null
        if (fields.reschedule != null) {
            schedule_text.text = fields.reschedule
        }
        archive_button.isVisible = fields.archive
        unarchive_button.isVisible = fields.unarchive
    }

    fun setListener(listener: Listener) {
        _listener = listener
    }

    fun isExpanded(): Boolean = bottom_layout.isVisible

    fun collapse() {
        bottom_layout.isVisible = false
        name_text_view.isSingleLine = true
        name_text_view.ellipsize = TextUtils.TruncateAt.MARQUEE
    }

    fun expand() {
        bottom_layout.isVisible = true
        name_text_view.isSingleLine = false
        name_text_view.maxLines = 3
        name_text_view.ellipsize = TextUtils.TruncateAt.END
    }
}

private fun setDateString(view: TextView, date: String?) {
    if (date == null) {
        view.visibility = View.INVISIBLE
    } else {
        view.isVisible = true
        view.text = date
    }
}