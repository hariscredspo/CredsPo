package com.haris.credspo.ui.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import com.haris.credspo.ApiInterface
import com.haris.credspo.BuildConfig
import com.haris.credspo.R
import com.haris.credspo.databinding.FragmentProfileBinding
import com.haris.credspo.models.ContentUriRequestBody
import com.haris.credspo.models.UserData
import com.haris.credspo.ui.TwoButtonDialogFragment
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import com.haris.credspo.models.DeleteResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })

        binding.profileImagePfp.setOnClickListener{
            val dialog = TwoButtonDialogFragment(
                { takePhoto() },
                { choosePhoto() },
                "Upload photo",  "Do you want to choose a photo or take a new one?", "TAKE PHOTO", "CHOOSE PHOTO")
            dialog.show(parentFragmentManager, "photoUploadMethodDialog")
        }

        val sharedPrefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val userData = sharedPrefs.getString("USER_DATA", null)
        token = sharedPrefs.getString("BEARER_TOKEN", null)

        userData?.let { json ->
            val convertedJson = GsonBuilder().create().fromJson(json, UserData::class.java)

            Glide.with(requireContext())
                .load(convertedJson.imagePath)
                .placeholder(R.drawable.placeholder_pfp)
                .centerCrop()
                .dontAnimate()
                .into(binding.profileImagePfp)

            binding.profileLabelName.text = "${convertedJson.firstName} ${convertedJson.lastName}"
        } ?: run {
            token?.let { token ->
                viewModel.getProfile(token)
            }
        }


        with(binding) {
            profileButtonLogout.setOnClickListener { logout() }
            profileButtonDelete.setOnClickListener {
                token?.let {
                    viewModel.deleteProfile(it)
                }
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
                binding.profileLabelName.text = "${it.firstName} ${it.lastName}"
            }
        }

        viewModel.pfpUpdateStatus.observeForever { status ->
            if(status == false) {
                Toast.makeText(requireContext(), "Could not update profile picture", Toast.LENGTH_SHORT).show()
            } else if(status == true) {
                Toast.makeText(requireContext(), "Successfully updated profile picture", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.deleteProfileStatus.observeForever { status ->
            if(status == true) {
                Toast.makeText(requireContext(), "Successfully deleted account", Toast.LENGTH_SHORT).show()
                logout()
            } else if(status == false) {
                Toast.makeText(requireContext(), "Could not delete account", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logout() {
        requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE).edit()
            .putString("BEARER_TOKEN", null)
            .putString("USER_DATA", null)
            .apply()

        if(viewModel.deleteProfileStatus.value != true) {
            Toast.makeText(requireContext(), "Successfully logged out", Toast.LENGTH_SHORT).show()
        }

        if(findNavController().currentDestination?.id == R.id.profile_fragment) {
            findNavController().navigate(R.id.action_profile_fragment_to_login_fragment)
        }
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
                if(uri == null) {
                    Toast.makeText(requireContext(), "Please choose a photo", Toast.LENGTH_SHORT).show()
                } else {
                    val imageBody = ContentUriRequestBody(requireContext().contentResolver, uri)
                    val imagePart = MultipartBody.Part.createFormData("image_path","${System.currentTimeMillis()}.png", imageBody)
                    viewModel.updatePfp(it, imagePart)
                    binding.profileImagePfp.setImageURI(uri)
                }
            }
        }
}