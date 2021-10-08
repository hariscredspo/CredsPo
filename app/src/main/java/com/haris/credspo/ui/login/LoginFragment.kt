package com.haris.credspo.ui.login

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
                findNavController().navigate(R.id.action_login_fragment_to_registration_fragment)
            }
            loginButton.setOnClickListener {
                loginProgressBar.visibility = View.VISIBLE
                viewModel.login(loginEdittextEmail.text.toString().trim(), loginEdittextPass.text.toString())
            }


            /*viewModel.loginStatus.observeForever {
                it?.let {
                    loginProgressBar.visibility = View.GONE
                    if (it) {
                        Log.e("LOGIN STATUS", "true")

                        viewModel.userResponseLiveData.value?.let { response ->
                            val validationAction = LoginFragmentDirections
                                .actionLoginFragmentToProfileFragment(
                                    response.user.firstName,
                                    response.user.lastName
                                )
                            findNavController().navigate(validationAction)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_LONG).show()
                    }
                }
            }*/
            viewModel.loginStatus.observeForever {
                it?.let {
                    loginProgressBar.visibility = View.GONE
                }
            }
            viewModel.userResponseLiveData.observeForever { userResponseLiveData ->
                userResponseLiveData?.let { livedata ->
                    viewModel.loginStatus.observeForever { loginStatus ->
                        loginStatus?.let { status ->
                            loginProgressBar.visibility = View.GONE
                            if(status) {
                                val validationAction = LoginFragmentDirections
                                    .actionLoginFragmentToProfileFragment(
                                        livedata.user.firstName,
                                        livedata.user.lastName
                                    )
                                findNavController().navigate(validationAction)
                            } else {
                                Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }
    }

}
