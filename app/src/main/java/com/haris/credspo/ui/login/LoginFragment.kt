package com.haris.credspo.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.haris.credspo.R
import com.haris.credspo.databinding.FragmentLoginBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.notify

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            loginLabelCreateAccount.setOnClickListener {
                if(findNavController().currentDestination?.id == R.id.login_fragment) {
                    findNavController().navigate(R.id.action_login_fragment_to_registration_fragment)
                }
            }
            loginButton.setOnClickListener {
                loginProgressBar.visibility = View.VISIBLE
                viewModel.login(loginEdittextEmail.text.toString().trim(), loginEdittextPass.text.toString())
            }

            var loginStatus = false
            viewModel.loginStatus.observeForever {
                it?.let {
                    loginStatus = it
                    loginProgressBar.visibility = View.GONE
                    if(!it) {
                        Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_LONG).show()
                    }
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

                        if(findNavController().currentDestination?.id == R.id.login_fragment) {
                            findNavController().navigate(R.id.action_login_fragment_to_profile_fragment)
                        }
                    }
                }
            }
        }
    }

}
