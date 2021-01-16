package com.example.howie.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.howie.core.TaskListIndex
import com.example.howie.databinding.FragmentMoveTaskBinding

class MoveTaskFragment : DialogFragment() {
    companion object {
        const val TASK_ID_ARGUMENT = "taskId"
        const val FROM_TASK_LIST_ARGUMENT = "fromTaskListId"
    }

    private val _viewModel: MoveTaskViewModel by viewModels {
        MoveTaskViewModelFactory(requireActivity().application)
    }
    private lateinit var _listener: MoveTaskFragmentListener

    interface MoveTaskFragmentListener {
        fun onTaskMoved()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _viewModel.taskId = requireArguments().getParcelable(TASK_ID_ARGUMENT)!!
        _viewModel.fromTaskList = requireArguments().getParcelable(FROM_TASK_LIST_ARGUMENT)!!
        val binding = FragmentMoveTaskBinding.inflate(LayoutInflater.from(context), null, false)
        val messageBoxBuilder =
            AlertDialog.Builder(requireActivity()).setView(binding.root)
                .setMessage("Move Task")
                .setPositiveButton("Ok") { _, _ ->
                    val selectedIndex = TaskListIndex(binding.taskListSpinner.selectedItemPosition)
                    _viewModel.moveToList(selectedIndex)
                    _listener.onTaskMoved()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
        _viewModel.taskListNames.observe(this) {
            val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, it)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.taskListSpinner.adapter = adapter
        }
        return messageBoxBuilder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            _listener = context as MoveTaskFragmentListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement MoveTaskFragmentListener")
        }
    }
}