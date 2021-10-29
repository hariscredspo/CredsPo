package com.haris.credspo.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haris.credspo.ApiInterface
import com.haris.credspo.models.UserData
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel: ViewModel() {
    val profileResponseData = MutableLiveData<UserData?>(null)
    val pfpUpdateStatus = MutableLiveData<Boolean?>(null)

    fun getProfile(token: String) {
        val call = ApiInterface.create().getProfile("Bearer $token")
        call.enqueue(object: Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                if(response.isSuccessful){
                    response.body()?.let { data ->
                        profileResponseData.postValue(data)
                        println("profile response data posted")
                    }
                }
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {}

        })
    }

    fun updatePfp(token: String, photoPart: MultipartBody.Part) {
        viewModelScope.launch {
            println(token)
            val response = ApiInterface.create().updateProfileImage("Bearer $token", photoPart)

            if(response.isSuccessful){
                pfpUpdateStatus.postValue(true)
                getProfile(token) // refresh
            } else {
                pfpUpdateStatus.postValue(false)
            }
        }

    }
}