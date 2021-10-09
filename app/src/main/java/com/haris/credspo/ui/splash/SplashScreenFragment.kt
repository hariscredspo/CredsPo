package com.haris.credspo.ui.splash

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.haris.credspo.ApiInterface
import com.haris.credspo.R
import com.haris.credspo.databinding.FragmentSplashBinding
import com.haris.credspo.ui.login.LoginViewModel
import kotlinx.coroutines.launch


class SplashScreenFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SplashScreenViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(SplashScreenViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            val sharedPrefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
            val token = sharedPrefs.getString("BEARER_TOKEN", null)

            token?.let {
                viewModel.getProfile(it)
            } ?: run {
                findNavController().navigate(R.id.action_splash_fragment_to_login_fragment)
            }

            viewModel.profileResponseData.observeForever { data ->
                data?.let {
                    val action =
                        SplashScreenFragmentDirections.actionSplashFragmentToProfileFragment(
                            it.firstName,
                            it.lastName
                        )
                    findNavController().navigate(action)
                }
            }
        }
    }

}