package com.haris.credspo.ui.registration

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.haris.credspo.ApiInterface
import com.haris.credspo.R
import com.haris.credspo.databinding.FragmentVerificationBinding
import com.haris.credspo.models.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerificationFragment : Fragment() {
    private var _binding: FragmentVerificationBinding? = null
    private val binding get() = _binding!!

    val args: VerificationFragmentArgs by navArgs()
    private lateinit var viewModel: VerificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVerificationBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(VerificationViewModel::class.java)
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
                with(args) {
                    ApiInterface.create().verify(
                        email, password,
                        "${verificationEdittext1.text}${verificationEdittext2.text}${verificationEdittext3.text}${verificationEdittext4.text}")
                        .enqueue(object: Callback<LoginResponse> {
                            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                                verificationProgressBar.visibility = View.VISIBLE
                                viewModel.login(email, password)
                            }

                            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {}
                        })
                }
            }


            var loginStatus = false
            viewModel.loginStatus.observeForever {
                it?.let {
                    loginStatus = it
                    verificationProgressBar.visibility = View.GONE
                }
            }
            viewModel.userResponseLiveData.observeForever {
                it?.let { livedata ->
                    if(loginStatus) {
                        val sharedPreferences = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()){
                            putString("BEARER_TOKEN", livedata.accessToken)
                            apply()
                        }

                        if(findNavController().currentDestination?.id == R.id.verification_fragment) {
                            findNavController().navigate(R.id.action_verification_fragment_to_profile_fragment)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_LONG).show()
                    }
                }
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
