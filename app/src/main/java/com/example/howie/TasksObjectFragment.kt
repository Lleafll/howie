package com.example.howie

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import kotlinx.android.synthetic.main.fragment_tasks_object.*

class TasksObjectFragment : Fragment(R.layout.fragment_tasks_object) {
    private val viewModel: TasksObjectViewModel by viewModels {
        TasksObjectViewModelFactory(requireActivity().application)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val position = requireArguments().getInt("position", 4)
        val unsnoozedTasks = when (position) {
            0 -> viewModel.doTasks
            1 -> viewModel.decideTasks
            2 -> viewModel.delegateTasks
            3 -> viewModel.dropTasks
            else -> error("$this: invalid tab position required")
        }
        val snoozedTasks = when (position) {
            0 -> viewModel.snoozedDoTasks
            1 -> viewModel.snoozedDecideTasks
            2 -> viewModel.snoozedDelegateTasks
            3 -> viewModel.snoozedDropTasks
            else -> error("$this: invalid tab position required")
        }
        setupView(unsnoozed_tasks_view, unsnoozedTasks, "Tasks", true)
        setupView(snoozed_tasks_view, snoozedTasks, "Snoozed Tasks", false)
    }

    private fun setupView(
        view: ExpandableTasksView,
        tasks: LiveData<List<Task>>,
        headerText: String,
        defaultExpandState: Boolean
    ) {
        view.setHeaderText(headerText)
        val taskAdapter = TaskAdapter {
            val intent = Intent(requireActivity().applicationContext, TaskActivity::class.java)
            intent.putExtra(TASK_ID, it)
            requireActivity().startActivityForResult(intent, TASK_REQUEST_CODE)
        }
        view.setAdapter(taskAdapter)
        tasks.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                view.visibility = View.GONE
            } else {
                view.visibility = View.VISIBLE
                view.setExpanded(defaultExpandState)
                it.let { taskAdapter.submitList(it) }
            }
        })
    }
}