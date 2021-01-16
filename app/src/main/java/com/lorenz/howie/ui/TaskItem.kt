package com.lorenz.howie.ui

import android.content.Context
import android.content.res.Configuration
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.howie.R
import com.example.howie.databinding.TaskItemBinding

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

    private val _binding = TaskItemBinding.inflate(LayoutInflater.from(context), this, true)
    private var _listener: Listener? = null

    init {
        inflate(context, R.layout.task_item, this)
        _binding.snoozeToTomorrow.setOnClickListener {
            _listener?.onSnoozeToTomorrowClicked()
        }
        _binding.removeSnooze.setOnClickListener {
            _listener?.onRemoveSnoozeClicked()
        }
        _binding.rescheduleButton.setOnClickListener {
            _listener?.onRescheduleClicked()
        }
        _binding.archiveButton.setOnClickListener {
            _listener?.onArchiveClicked()
        }
        _binding.unarchiveButton.setOnClickListener {
            _listener?.onUnarchiveClicked()
        }
        _binding.editButton.setOnClickListener {
            _listener?.onEditClicked()
        }
        setOnClickListener {
            _listener?.onSelected()
        }
    }

    fun setFields(fields: TaskItemFields) {
        _binding.nameTextView.text = fields.name
        setDateString(_binding.dueTextView, fields.due)
        setDateString(_binding.snoozedTextView, fields.snoozed)
        setDateString(_binding.archivedTextView, fields.archived)
        _binding.snoozeToTomorrowLayout.isVisible = fields.snoozedToTomorrow
        _binding.removeSnoozeLayout.isVisible = fields.removeSnoozed
        _binding.rescheduleLayout.isVisible = fields.reschedule != null
        if (fields.reschedule != null) {
            _binding.scheduleText.text = fields.reschedule
        }
        _binding.archiveLayout.isVisible = fields.archive
        _binding.unarchiveLayout.isVisible = fields.unarchive
    }

    fun setListener(listener: Listener) {
        _listener = listener
    }

    fun isExpanded(): Boolean = _binding.bottomLayout.isVisible

    fun collapse() {
        _binding.bottomLayout.isVisible = false
        _binding.nameTextView.isSingleLine = true
        _binding.nameTextView.ellipsize = TextUtils.TruncateAt.MARQUEE
        setCollapsedBackgroundColor()
    }

    private fun setCollapsedBackgroundColor() {
        setLayoutBackgroundColor(if (isNightMode()) R.color.taskItemCollapsedColorDark else R.color.taskItemCollapsedColorLight)
    }

    fun expand() {
        _binding.bottomLayout.isVisible = true
        _binding.nameTextView.isSingleLine = false
        _binding.nameTextView.maxLines = 3
        _binding.nameTextView.ellipsize = TextUtils.TruncateAt.END
        setExpandedBackgroundColor()
    }

    private fun setExpandedBackgroundColor() {
        setLayoutBackgroundColor(if (isNightMode()) R.color.taskItemExpandedColorDark else R.color.taskItemExpandedColorLight)
    }

    private fun setLayoutBackgroundColor(colorCode: Int) {
        _binding.constraintLayout.setBackgroundColor(ContextCompat.getColor(context, colorCode))
    }

    private fun isNightMode(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
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