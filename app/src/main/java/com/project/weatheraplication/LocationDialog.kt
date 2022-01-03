package com.project.weatheraplication

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment

class LocationDialog: AppCompatDialogFragment() {

    private lateinit var editCity: EditText
    private lateinit var listener: LocationDialogListener

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.Dialog)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_signin, null)

            builder.setView(inflater.inflate(R.layout.dialog_signin, null))
                .setPositiveButton(R.string.ok) { _, _ ->
                    editCity = ((dialog?.findViewById(R.id.location)) as EditText)
                    listener.applyText(editCity.text.toString())
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                }
            editCity = view.findViewById(R.id.location)

            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as LocationDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString() +
                        "must implement LocationDialogListener"
            )
        }
    }

    interface LocationDialogListener {
        fun applyText(city: String)
    }
}