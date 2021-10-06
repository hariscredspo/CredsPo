package com.haris.credspo.ui.registration

import android.content.res.ColorStateList
import android.graphics.Color
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
    var openedBirthYearInfo = false
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

        var birthYearList = listOf<String>("1", "2", "3");
        var countryList = listOf<String>("Montenegro", "Russia", "Nigeria", "Japan");

        val birthYearAdapter = ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, birthYearList)
        birthYearAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        val countryAdapter = ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, countryList)
        countryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)

        binding.registrationSpinnerBirthYear.adapter = birthYearAdapter
        binding.registrationSpinnerCountry.adapter = countryAdapter

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

        binding.registrationImageBirthYearInfoBackground.setOnClickListener {
            if(!openedBirthYearInfo) {
                binding.registrationImageBirthYearInfo.setTint(R.color.cyan)
                binding.registrationImageBirthYearInfoBackground.setImageResource(R.drawable.question_mark_background_checked)

                binding.registrationImageBirthYearInfoWindow.visibility = View.VISIBLE
                binding.registrationTextBirthYearInfo.visibility = View.VISIBLE

                openedBirthYearInfo = true
            } else {
                binding.registrationImageBirthYearInfo.clearTint()
                binding.registrationImageBirthYearInfoBackground.setImageResource(R.drawable.question_mark_background_unchecked)

                binding.registrationImageBirthYearInfoWindow.visibility = View.GONE
                binding.registrationTextBirthYearInfo.visibility = View.GONE

                openedBirthYearInfo = false
            }
        }
    }

    private fun ImageView.setTint(@ColorRes colorRes: Int) {
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)))
    }
    private fun ImageView.clearTint() {
        ImageViewCompat.setImageTintList(this, null)
    }
}