package com.haris.credspo.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.haris.credspo.R

class ConfirmationDialogFragment(
    private val confirmClickListener: View.OnClickListener,
    private val confirmationMsg: String,
) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_confirmation_popup, container, false)

        dialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.requestFeature(Window.FEATURE_NO_TITLE)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.confirmation_popup_label_message).text = confirmationMsg
        view.findViewById<Button>(R.id.confirmation_popup_button_yes).setOnClickListener {
            confirmClickListener.onClick(view)
            dismiss()
        }
        view.findViewById<Button>(R.id.confirmation_popup_button_no).setOnClickListener {
            dismiss()
        }
    }
}