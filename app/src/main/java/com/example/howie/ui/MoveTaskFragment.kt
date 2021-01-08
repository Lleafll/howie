package com.example.howie.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.howie.R

class MoveTaskFragment : DialogFragment() {
    companion object {
        const val TASK_ID = "taskId"
        const val FROM_TASK_LIST = "fromTaskListId"
    }

    private val viewModel: MoveTaskViewModel by viewModels {
        MoveTaskViewModelFactory(requireActivity().application)
    }
    private lateinit var listener: MoveTaskFragmentListener

    interface MoveTaskFragmentListener {
        fun onTaskMoved()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel.taskId = requireArguments().getInt(TASK_ID)
        viewModel.fromTaskList = requireArguments().getInt(FROM_TASK_LIST)
        val messageBoxBuilder =
            AlertDialog.Builder(requireActivity()).setView(R.layout.fragment_move_task)
                .setMessage("Move Task")
                .setPositiveButton("Ok") { _, _ ->
                    val spinner: Spinner = dialog!!.findViewById(R.id.task_list_spinner)
                    val selectedIndex = spinner.selectedItemPosition
                    viewModel.moveToList(selectedIndex)
                    listener.onTaskMoved()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
        val dialog = messageBoxBuilder.create()
        val spinner: Spinner = this.dialog!!.findViewById(R.id.task_list_spinner)
        viewModel.taskListNames.observe(this) {
            val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, it)
            spinner.adapter = adapter
        }
        return dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as MoveTaskFragmentListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement MoveTaskFragmentListener")
        }
    }
}