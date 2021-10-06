package com.haris.credspo.ui.registration

import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.haris.credspo.ApiInterface
import com.haris.credspo.R
import com.haris.credspo.databinding.FragmentRegistrationBinding
import com.haris.credspo.models.CountryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        var birthYearList = listOfIntegersInRange(1900..Calendar.getInstance().get(Calendar.YEAR))

        val birthYearAdapter = ArrayAdapter<Int>(requireContext(), R.layout.support_simple_spinner_dropdown_item, birthYearList)
        birthYearAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        binding.registrationSpinnerBirthYear.adapter = birthYearAdapter

        var countryList = mutableListOf<String>()

        val countryAdapter = ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, countryList)
        countryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)

        val call = ApiInterface.create().getCountries()
        call.enqueue(object: Callback<CountryResponse> {
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
                Toast.makeText(requireContext(), "Could not load country list", Toast.LENGTH_SHORT).show()
            }

        })


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

    private fun getCountries(): List<String> {
        var countries = mutableListOf<String>()

        val call = ApiInterface.create().getCountries()
        call.enqueue(object: Callback<CountryResponse> {
            override fun onResponse(
                call: Call<CountryResponse>,
                response: Response<CountryResponse>
            ) {
                if(response.isSuccessful){
                    response.body()?.let {
                        it.data.forEach { country ->
                            countries.add(country.name)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CountryResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Could not load country list", Toast.LENGTH_SHORT).show()
            }

        })

        while(true){
            println("Waiting for response")
            if(countries.isNotEmpty())
                break
        }

        return countries
    }
    private fun listOfIntegersInRange(range: IntRange): List<Int> {
        var list = mutableListOf<Int>()
        for(n in range)
            list.add(n)

        return list
    }
}