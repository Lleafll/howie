package com.lorenz.howie.ui

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.howie.R
import com.google.android.material.textfield.TextInputEditText

const val TASK_LIST_ID_ARGUMENT = "taskListId"

class RenameTaskListFragment : DialogFragment() {
    lateinit var onOkClicked: (String) -> Unit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val messageBoxBuilder =
            AlertDialog.Builder(requireActivity()).setView(R.layout.fragment_rename_task_list)
                .setMessage("Rename List")
                .setPositiveButton("Ok") { _, _ ->
                    val textEdit: TextInputEditText = dialog!!.findViewById(R.id.new_task_list_name)
                    onOkClicked(textEdit.text.toString())
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
        val dialog = messageBoxBuilder.create()
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        return dialog
    }
}