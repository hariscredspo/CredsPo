package com.haris.credspo.ui.registration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haris.credspo.ApiInterface
import com.haris.credspo.models.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerificationViewModel: ViewModel() {
    val loginStatus = MutableLiveData<Boolean?>(null)
    val userResponseLiveData = MutableLiveData<LoginResponse?>(null)

    fun login(email: String, password: String) {
        ApiInterface.create().login(email, password).enqueue(object: Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                response.body()?.let {
                    loginStatus.postValue(true)
                    userResponseLiveData.postValue(it)
                } ?: run {
                    loginStatus.postValue(false)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginStatus.postValue(false)
            }

        })
    }
}