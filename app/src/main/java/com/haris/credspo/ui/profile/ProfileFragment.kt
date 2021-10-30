package com.haris.credspo.ui.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import com.haris.credspo.BuildConfig
import com.haris.credspo.R
import com.haris.credspo.databinding.FragmentProfileBinding
import com.haris.credspo.models.ContentUriRequestBody
import com.haris.credspo.models.UserData
import com.haris.credspo.ui.ConfirmationDialogFragment
import okhttp3.MultipartBody
import java.io.File
import android.content.Intent

import androidx.core.app.ActivityCompat.startActivityForResult




class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val args: ProfileFragmentArgs by navArgs()
    private lateinit var viewModel: ProfileViewModel

    private var token: String? = null

    private var newPhotoUri: Uri? = null

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
        binding.profileImagePfp.setOnClickListener{
            val dialog = ConfirmationDialogFragment(
                { takePhoto() },
                { choosePhoto() },
                "Upload photo",  "Do you want to choose a photo or take a new one?", "TAKE PHOTO", "CHOOSE PHOTO")
            dialog.show(parentFragmentManager, "ConfirmationDialogFragment")
        }

        val sharedPrefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val userData = sharedPrefs.getString("USER_DATA", null)
        token = sharedPrefs.getString("BEARER_TOKEN", null)

        userData?.let { json ->
            val convertedJson = GsonBuilder().create().fromJson(json, UserData::class.java)

            Glide.with(this)
                .load(convertedJson.imagePath)
                .centerCrop()
                .into(binding.profileImagePfp)

            binding.profileLabelName.text = "${convertedJson.firstName} ${convertedJson.lastName}"
        } ?: run {
            token?.let { token ->
                viewModel.getProfile(token)
            }
        }

        viewModel.profileResponseData.observeForever { data ->
            data?.let {
                Glide.with(this)
                    .load(it.imagePath)
                    .centerCrop()
                    .into(binding.profileImagePfp)

                val convertedData = GsonBuilder().create().toJson(it)
                sharedPrefs.edit().putString("USER_DATA", convertedData).apply()
                println("shared preferences updated")
                binding.profileLabelName.text = "${it.firstName} ${it.lastName}"
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

    private fun takePhoto() {
        newPhotoUri = getTemporaryFileUri()
        takeImageResult.launch(newPhotoUri)
    }
    private fun choosePhoto() {
        choosePhotoResult.launch("image/*")
    }

    private fun getTemporaryFileUri(): Uri {
        val tempFile = File.createTempFile("temp_image_file", ".png", requireActivity().cacheDir)
        tempFile.createNewFile()
        tempFile.deleteOnExit()

        return FileProvider.getUriForFile(
            requireContext().applicationContext,
            "${BuildConfig.APPLICATION_ID}.provider",
            tempFile
        )
    }

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                newPhotoUri?.let { uri ->
                    token?.let {
                        val imageBody = ContentUriRequestBody(requireContext().contentResolver, uri)
                        val imagePart = MultipartBody.Part.createFormData("image_path","${System.currentTimeMillis()}.png", imageBody)
                        viewModel.updatePfp(it, imagePart)
                    }
                    binding.profileImagePfp.setImageURI(uri)
                }
            }
        }

    private val choosePhotoResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            token?.let {
                val imageBody = ContentUriRequestBody(requireContext().contentResolver, uri)
                val imagePart = MultipartBody.Part.createFormData("image_path","${System.currentTimeMillis()}.png", imageBody)
                viewModel.updatePfp(it, imagePart)
            }
            binding.profileImagePfp.setImageURI(uri)
        }
}