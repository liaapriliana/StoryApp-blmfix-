package com.intermediate.storyapp1.data.api

import androidx.viewbinding.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    fun getInterceptor(token : String?) : OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                    .let(chain::proceed)
            }
            .build()

        return okHttpClient
    }

    fun getRetrofit(token : String?): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .client(getInterceptor(token))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getService(token : String? = null) = getRetrofit(token).create(ApiService::class.java)
}
