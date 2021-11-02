package com.haris.credspo.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.haris.credspo.R
import com.haris.credspo.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashScreenFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            delay(1500)

            val sharedPrefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
            val token = sharedPrefs.getString("BEARER_TOKEN", null)

            token?.let {
                findNavController().navigate(R.id.action_splash_fragment_to_profile_fragment)
            } ?: run {
                val direction = SplashScreenFragmentDirections.actionSplashFragmentToLoginFragment()
                val extras = FragmentNavigatorExtras(
                    binding.splashLogo to "login_fragment_logo",
                    binding.splashName to "login_fragment_name")
                findNavController().navigate(direction, extras)
            }
        }
    }

}