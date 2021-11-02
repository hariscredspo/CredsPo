package com.haris.credspo.ui.registration

import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.haris.credspo.ApiInterface
import com.haris.credspo.R
import com.haris.credspo.databinding.FragmentRegistrationBinding
import com.haris.credspo.models.CountryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.lifecycle.lifecycleScope
import com.haris.credspo.models.RegistrationResponse
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class RegistrationFragment: Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private var agreedToTerms = false
    private var openedBirthYearInfo = false

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

        setupBirthYearInfoBox()
        setupBirthYearSpinner()
        setupCountrySpinner()
        setupTermsAndConditionsCheckbox()

        binding.registrationButton.setOnClickListener {
            if(validForm()) {

                with(binding) {
                    lifecycleScope.launch {
                        ApiInterface.create().register(
                            registrationEdittextFirstName.text.toString(),
                            registrationEdittextLastName.text.toString(),
                            registrationEdittextEmail.text.toString(),
                            registrationEdittextPass.text.toString(),
                            registrationEdittextRepeatPass.text.toString(),
                            (registrationSpinnerCountry.selectedItemId+1).toString(),
                            registrationSpinnerBirthYear.selectedItem.toString()
                        ).enqueue(object: Callback<RegistrationResponse> {
                            override fun onResponse(
                                call: Call<RegistrationResponse>,
                                response: Response<RegistrationResponse>
                            ) {
                                if(findNavController().currentDestination?.id == R.id.registration_fragment) {
                                    findNavController().navigate(
                                        RegistrationFragmentDirections.actionRegistrationFragmentToVerificationFragment(
                                            registrationEdittextEmail.text.toString(),
                                            registrationEdittextPass.text.toString(),
                                        )
                                    )
                                }
                            }

                            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                                Toast.makeText(requireContext(), "Could not register user", Toast.LENGTH_SHORT).show()
                            }

                        })
                    }
                }
            }
        }
    }


    private fun setupBirthYearSpinner() {
        val sdf = SimpleDateFormat("yyyy", Locale.getDefault())
        val currentYear: Int = sdf.format(Date()).toInt()

        var birthYearList = listOfIntegersInRange(1910..currentYear)

        val birthYearAdapter = ArrayAdapter<Int>(requireContext(), R.layout.support_simple_spinner_dropdown_item, birthYearList)
        birthYearAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        binding.registrationSpinnerBirthYear.adapter = birthYearAdapter
    }

    private fun setupCountrySpinner() {
        var countryList = mutableListOf<String>()

        val countryAdapter = ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, countryList)
        countryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)

        ApiInterface.create()
            .getCountries().enqueue(object: Callback<CountryResponse> {
            override fun onResponse(
                call: Call<CountryResponse>,
                response: Response<CountryResponse>
            ) {
                if(response.isSuccessful){
                    response.body()?.let {
                        it.data.forEach { country ->
                            countryList.add(country.name)
                            println("added country " + country.id.toString())
                            binding.registrationSpinnerCountry.adapter = countryAdapter
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CountryResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Could not load country list", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun setupTermsAndConditionsCheckbox() {
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
    }

    private fun setupBirthYearInfoBox() {
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

    private fun validForm(): Boolean {
        with(binding) {
            if(registrationEdittextFirstName.text.length < 3){
                registrationInputFirstName.isErrorEnabled = true
                registrationInputFirstName.error = resources.getString(R.string.requirement_first_name)
                return false
            } else {
                registrationInputFirstName.error = null
            }

            if(registrationEdittextLastName.text.length < 3){
                registrationInputLastName.error = resources.getString(R.string.requirement_last_name)
                return false
            } else {
                registrationInputLastName.error = null
            }

            if(!isValidEmail(registrationEdittextEmail.text.toString())){
                registrationInputEmail.error = resources.getString(R.string.requirement_email)
                return false
            } else {
                registrationInputEmail.error = null
            }

            if(registrationEdittextPass.text.length < 8){
                registrationInputPass.error = resources.getString(R.string.requirement_password)
                return false
            } else {
                registrationInputPass.error = null
            }

            if(registrationEdittextRepeatPass.text.toString() != registrationEdittextPass.text.toString()){
                registrationInputRepeatPass.error = resources.getString(R.string.requirement_repeat_password)
                return false
            } else {
                registrationInputRepeatPass.error = null
            }

            if(!agreedToTerms)
                return false

        }
        return true
    }

    private fun isValidEmail(email: String): Boolean {
        if(email.isEmpty())
            return false
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun ImageView.setTint(@ColorRes colorRes: Int) {
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)))
    }
    private fun ImageView.clearTint() {
        ImageViewCompat.setImageTintList(this, null)
    }

    private fun listOfIntegersInRange(range: IntRange): List<Int> {
        var list = mutableListOf<Int>()
        for(n in range)
            list.add(n)

        return list
    }

}