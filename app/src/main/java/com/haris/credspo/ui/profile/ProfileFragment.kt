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
import com.google.gson.GsonBuilder
import com.haris.credspo.R
import com.haris.credspo.databinding.FragmentProfileBinding
import com.haris.credspo.models.UserData

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
        val userData = sharedPrefs.getString("USER_DATA", null)
        val token = sharedPrefs.getString("BEARER_TOKEN", null)

        userData?.let { json ->
            val convertedJson = GsonBuilder().create().fromJson(json, UserData::class.java)
            binding.profileLabelName.text = "${convertedJson.firstName} ${convertedJson.lastName}"
        } ?: run {
            token?.let { token ->
                viewModel.getProfile(token)

                viewModel.profileResponseData.observeForever { data ->
                    data?.let {
                        val convertedData = GsonBuilder().create().toJson(it)
                        sharedPrefs.edit().putString("USER_DATA", convertedData).apply()
                        binding.profileLabelName.text = "${it.firstName} ${it.lastName}"
                    }
                }
            }
        }


        with(binding) {
            profileButtonLogout.setOnClickListener { logout() }
            profileButtonDelete.setOnClickListener { logout() }
        }
    }

    private fun logout() {
        findNavController().navigate(R.id.action_profile_fragment_to_login_fragment)
        requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE).edit()
            .putString("BEARER_TOKEN", null)
            .putString("USER_DATA", null)
            .apply()
    }
}