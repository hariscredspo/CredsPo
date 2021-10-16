package com.haris.credspo.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.haris.credspo.R
import com.haris.credspo.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val args: ProfileFragmentArgs by navArgs()
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPrefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val token = sharedPrefs.getString("BEARER_TOKEN", null)

        token?.let {
            viewModel.getProfile(it)
        }

        viewModel.profileResponseData.observeForever { data ->
            data?.let {
                with(binding) {
                    profileLabelName.text = "${it.firstName} ${it.lastName}"
                    profileButtonLogout.setOnClickListener { logout() }
                    profileButtonDelete.setOnClickListener { logout() }
                    profileButtonActivityHistory.setOnClickListener { findNavController().navigate(R.id.action_profile_fragment_to_activity_history_fragment) }
                }
            }
        }
    }

    private fun logout() {
        findNavController().navigate(R.id.action_profile_fragment_to_login_fragment)
    }
}
