package com.example.newsapp2.data.network.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val key = "c8556aedf85d4a5b80ad98ac35763a7a"

    private const val BASE_URL = "https://newsapi.org/v2/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor {chain ->
            val original = chain.request()

            val requestBuilder = original.newBuilder()
                .addHeader("authorization", key)
                .method(original.method, original.body)

            val request = requestBuilder.build()

            chain.proceed(request)
        }
        .retryOnConnectionFailure(true)
        .build()

    val instance: RetrofitService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)

    }
}