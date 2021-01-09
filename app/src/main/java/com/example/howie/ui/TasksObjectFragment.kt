package com.example.howie.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.example.howie.R
import com.example.howie.core.Task
import kotlinx.android.synthetic.main.fragment_tasks_object.*

class TasksObjectFragment : Fragment(R.layout.fragment_tasks_object) {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews(viewModel, requireActivity())
    }
}

private fun TasksObjectFragment.setupViews(viewModel: MainViewModel, activity: FragmentActivity) {
    unsnoozed_tasks_view.setHeaderText("Tasks")
    snoozed_tasks_view.setHeaderText("Snoozed Tasks")
    val unsnoozedAdapter = buildTaskAdapter(viewModel, activity)
    val snoozedAdapter = buildTaskAdapter(viewModel, activity)
    viewModel.tasks.observe(viewLifecycleOwner) { unarchivedTasks ->
        setTasks(unsnoozedAdapter, unsnoozed_tasks_view, unarchivedTasks.unsnoozed, true)
        setTasks(snoozedAdapter, snoozed_tasks_view, unarchivedTasks.snoozed, false)
    }
}

private fun setTasks(
    taskAdapter: TaskAdapter,
    view: ExpandableTasksView,
    tasks: List<Task>,
    defaultExpandState: Boolean
) {
    view.setAdapter(taskAdapter)
    if (tasks.isEmpty()) {
        view.visibility = View.GONE
    } else {
        view.visibility = View.VISIBLE
        view.setExpanded(defaultExpandState)
        taskAdapter.submitList(tasks)
    }
}

private fun buildTaskAdapter(viewModel: MainViewModel, activity: FragmentActivity) =
    TaskAdapter(object : TaskAdapter.Listener {
        override fun onSnoozeToTomorrowClicked(position: Int) {
            viewModel.snoozeToTomorrow(position)
        }

        override fun onRemoveSnoozeClicked(position: Int) {
            TODO("Implement")
        }

        override fun onRescheduleClicked(position: Int) {
            TODO("Implement")
        }

        override fun onArchiveClicked(position: Int) {
            TODO("Implement")
        }

        override fun onUnarchiveClicked(position: Int) {
            TODO("Implement")
        }

        override fun onEditClicked(position: Int) {
            val intent = Intent(activity.applicationContext, TaskActivity::class.java)
            intent.putExtra(TaskActivity.TASK_ID, position)
            activity.startActivityForResult(intent, TASK_REQUEST_CODE)
        }
    })