package com.haris.credspo

import com.haris.credspo.models.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

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

    @GET("/api/activity-history")
    fun getActivityHistory(
        @Header("Authorization") token: String,
        @Query("badge_type_id") badgeTypeID: Int = 0,
        @Query("badge_id") badgeID: Int = 0,
    ): Call<ActivityHistoryResponse>

    @GET("/api/user-skills")
    fun getUserSkills(
        @Header("Authorization") token: String,
    ): Call<UserBadgesResponse>

    @GET("/api/user-attributes")
    fun getUserAttributes(
        @Header("Authorization") token: String,
    ): Call<UserBadgesResponse>

    @GET("/api/user-waypoints")
    fun getUserWaypoints(
        @Header("Authorization") token: String,
    ): Call<UserBadgesResponse>

    @POST("/api/auth/login")
    fun login(
        @Query("email") email: String,
        @Query("password") password: String,
    ): Call<LoginResponse>

    @DELETE("/api/delete-activity")
    fun deleteActivity(
        @Header("Authorization") token: String,
        @Query("id") id: Int = 0,
    ): Call<DeleteResponse>
}