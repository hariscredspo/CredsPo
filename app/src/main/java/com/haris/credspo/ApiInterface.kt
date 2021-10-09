package com.haris.credspo

import com.haris.credspo.models.CountryResponse
import com.haris.credspo.models.LoginResponse
import com.haris.credspo.models.UserData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {
    companion object {
        val BASE_URL = "https://credspo-dev.amplitudo.me"
        fun create(): ApiInterface {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(client)
                .build()

            return retrofit.create(ApiInterface::class.java)
        }
    }

    @GET("/api/countries")
    fun getCountries(): Call<CountryResponse>

    @GET("/api/user-profile")
    fun getProfile(
        @Header("Authorization") token: String,
    ): Call<UserData>

    @POST("/api/auth/login")
    fun login(
        @Query("email") email: String,
        @Query("password") password: String,
    ): Call<LoginResponse>
}