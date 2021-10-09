package com.haris.credspo.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haris.credspo.ApiInterface
import com.haris.credspo.models.UserData
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashScreenViewModel: ViewModel() {
    val profileResponseData = MutableLiveData<UserData?>(null)

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
}