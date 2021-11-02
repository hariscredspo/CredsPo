package com.haris.credspo.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.haris.credspo.R

class TwoButtonDialogFragment(
    private val buttonOneListener: View.OnClickListener,
    private val buttonTwoListener: View.OnClickListener,
    private val titleText: String,
    private val descriptionText: String,
    private val buttonOneText: String,
    private val buttonTwoText: String,
) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_two_button_popup, container, false)

        dialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.requestFeature(Window.FEATURE_NO_TITLE)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.confirmation_popup_label_message).text = descriptionText
        view.findViewById<TextView>(R.id.confirmation_popup_label_title).text = titleText
        view.findViewById<TextView>(R.id.confirmation_popup_button_yes).text = buttonOneText
        view.findViewById<TextView>(R.id.confirmation_popup_button_no).text = buttonTwoText
        view.findViewById<Button>(R.id.confirmation_popup_button_yes).setOnClickListener {
            buttonOneListener.onClick(view)
            dismiss()
        }
        view.findViewById<Button>(R.id.confirmation_popup_button_no).setOnClickListener {
            buttonTwoListener.onClick(view)
            dismiss()
        }
    }
}