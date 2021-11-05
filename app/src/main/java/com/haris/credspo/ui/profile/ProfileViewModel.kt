package com.haris.credspo.ui.profile

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.haris.credspo.ApiInterface
import com.haris.credspo.models.DeleteResponse
import com.haris.credspo.models.MessageResponse
import com.haris.credspo.models.UserData
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel: ViewModel() {
    val profileResponseData = MutableLiveData<UserData?>(null)
    val pfpUpdateStatus = MutableLiveData<Boolean?>(null)
    val deleteProfileStatus = MutableLiveData<Boolean?>(null)

    fun getProfile(token: String) {
        val call = ApiInterface.create().getProfile("Bearer $token")
        call.enqueue(object: Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                if(response.isSuccessful){
                    response.body()?.let { data ->
                        profileResponseData.postValue(data)
                    }
                }
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {}
        })
    }

    fun updatePfp(token: String, photoPart: MultipartBody.Part) {
        viewModelScope.launch {
            ApiInterface.create().updateProfileImage("Bearer $token", photoPart)
                .enqueue(object: Callback<MessageResponse> {
                    override fun onResponse(
                        call: Call<MessageResponse>,
                        response: Response<MessageResponse>
                    ) {
                        pfpUpdateStatus.postValue(true)
                        getProfile(token) // refresh
                    }

                    override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                        pfpUpdateStatus.postValue(false)
                    }

                })
        }
    }

    fun deleteProfile(token: String) {
        viewModelScope.launch {
            ApiInterface.create().deleteUser("Bearer $token")
                .enqueue(object: Callback<DeleteResponse> {
                    override fun onResponse(
                        call: Call<DeleteResponse>,
                        response: Response<DeleteResponse>
                    ) {
                        deleteProfileStatus.postValue(true)
                    }

                    override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                        deleteProfileStatus.postValue(false)
                    }

                })
        }
    }
}