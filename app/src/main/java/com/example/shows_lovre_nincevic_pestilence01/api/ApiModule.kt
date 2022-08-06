package com.example.shows_lovre_nincevic_pestilence01.api

import android.content.Context
import android.content.SharedPreferences
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiModule {
    private const val BASE_URL = "https://tv-shows.infinum.academy/"

    lateinit var retrofit: ShowsApiService

    fun initRetrofit(context: Context) {
        val okhttp = OkHttpClient.Builder()
            .addInterceptor(ChuckerInterceptor.Builder(context).build())
            .addInterceptor(HeaderInterceptor(context))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okhttp)
            .build()
            .create(ShowsApiService::class.java)
    }

    fun initRetrofitAws(context: Context, url: String) {
        val okhttp = OkHttpClient.Builder()
            .addInterceptor(ChuckerInterceptor.Builder(context).build())
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okhttp)
            .build()
            .create(ShowsApiService::class.java)
    }

    class HeaderInterceptor(context: Context) : Interceptor {

        private val sharedPreferences: SharedPreferences = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)

        override fun intercept(chain: Interceptor.Chain): Response = chain.run {

            val accessToken = sharedPreferences.getString("accessToken", "empty")
            val client = sharedPreferences.getString("client", "empty")
            val uid = sharedPreferences.getString("uid", "empty")

            proceed(
                request()
                    .newBuilder()
                    .addHeader("token-type", "Bearer")
                    .addHeader("access-token", accessToken!!)
                    .addHeader("client", client!!)
                    .addHeader("uid", uid!!)
                    .build()
            )
        }
    }
}