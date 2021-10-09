package com.haris.credspo.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haris.credspo.ApiInterface
import com.haris.credspo.models.UserResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel: ViewModel() {
    val loginStatus = MutableLiveData<Boolean?>(null)
    val userResponseLiveData = MutableLiveData<UserResponse?>(null)

    fun login(email: String, password: String) {
        ApiInterface.create().login(email, password).enqueue(object: Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                response.body()?.let {
                    loginStatus.postValue(true)
                    userResponseLiveData.postValue(it)
                } ?: run {
                    loginStatus.postValue(false)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                loginStatus.postValue(false)
            }

        })
    }
}