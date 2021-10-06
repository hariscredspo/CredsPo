package com.haris.credspo.ui.registration

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.haris.credspo.R
import com.haris.credspo.databinding.FragmentRegistrationBinding

class RegistrationFragment: Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    var agreedToTerms = false
    var openedTermsHelpWindow = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var birth_year_list = listOf<String>("1", "2", "3");
        var country_list = listOf<String>("Montenegro", "Russia", "Nigeria", "Japan");

        val birth_year_adapter = ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, birth_year_list)
        birth_year_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        val country_adapter = ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, country_list)
        country_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)

        binding.registrationSpinnerBirthYear.adapter = birth_year_adapter
        binding.registrationSpinnerCountry.adapter = country_adapter

        binding.registrationImageTermsAndPolicyCheckboxBackground.setOnClickListener {
            if(!agreedToTerms){
                binding.registrationImageTermsAndPolicyCheckbox.setColorFilter(Color.argb(255, 255, 255, 255))
                binding.registrationImageTermsAndPolicyCheckboxBackground.setImageResource(R.drawable.checkbox_background_checked)
                agreedToTerms = true
            } else {
                binding.registrationImageTermsAndPolicyCheckbox.setColorFilter(Color.argb(0, 0, 0, 0))
                binding.registrationImageTermsAndPolicyCheckboxBackground.setImageResource(R.drawable.checkbox_background_unchecked)
                agreedToTerms = false
            }

        }

        binding.registrationImageBirthYearHelpBackground.setOnClickListener {
            if(!openedTermsHelpWindow) {
                binding.registrationImageBirthYearHelp.setTint(R.color.cyan)
                binding.registrationImageBirthYearHelpBackground.setImageResource(R.drawable.question_mark_background_checked)

                binding.registrationImageBirthYearHelpWindow.visibility = View.VISIBLE
                binding.registrationImageBirthYearHelpText.visibility = View.VISIBLE

                openedTermsHelpWindow = true
            } else {
                binding.registrationImageBirthYearHelp.clearTint()
                binding.registrationImageBirthYearHelpBackground.setImageResource(R.drawable.question_mark_background_unchecked)

                binding.registrationImageBirthYearHelpWindow.visibility = View.GONE
                binding.registrationImageBirthYearHelpText.visibility = View.GONE

                openedTermsHelpWindow = false
            }
        }
    }
    fun ImageView.setTint(@ColorRes colorRes: Int) {
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)))
    }
    fun ImageView.clearTint() {
        ImageViewCompat.setImageTintList(this, null)
    }
}