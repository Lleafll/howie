package com.example.howie

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class RenameTaskListFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val messageBoxBuilder =
            AlertDialog.Builder(activity!!).setView(R.layout.fragment_rename_task_list)
                .setMessage("Rename List")
                .setPositiveButton("Ok") { _, _ ->
                    // TODO: Implement
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
        return messageBoxBuilder.create()
    }
}