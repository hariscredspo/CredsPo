package com.haris.credspo.ui.registration

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.haris.credspo.R
import com.haris.credspo.databinding.FragmentVerificationBinding

class VerificationFragment : Fragment() {
    private var _binding: FragmentVerificationBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            verificationEdittext1.setOnFocusChangeListener { view, b ->
                view.setBackgroundResource(R.drawable.rect_rounded_white_outline)
            }

            linkInputFields(verificationEdittext1, verificationEdittext2)
            linkInputFields(verificationEdittext2, verificationEdittext3)
            linkInputFields(verificationEdittext3, verificationEdittext4)

            verificationEdittext4.doAfterTextChanged {
                findNavController().navigate(R.id.action_verification_fragment_to_profile_fragment)
            }
        }
    }

    private fun linkInputFields(current: EditText, next: EditText) {
        current.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isNotEmpty()){
                    next.setBackgroundResource(R.drawable.rect_rounded_white_outline)
                    next.requestFocus()
                }
            }
        })
    }
}
