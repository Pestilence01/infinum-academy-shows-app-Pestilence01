package com.example.shows_lovre_nincevic_pestilence01.api

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiModule {
    private const val BASE_URL = "https://tv-shows.infinum.academy/"

    lateinit var retrofit: ShowsApiService

    fun initRetrofit(context: Context) {
        val okhttp = OkHttpClient.Builder()
            .addInterceptor(ChuckerInterceptor.Builder(context).build())
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
}