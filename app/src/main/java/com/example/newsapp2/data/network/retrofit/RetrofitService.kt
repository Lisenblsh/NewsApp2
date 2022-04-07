package com.example.newsapp2.data.network.retrofit

import com.example.newsapp2.data.network.NewsModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    @GET("everything?")
    suspend fun getNews(
        @Query("q") q: String,
        @Query("sortBy") sortBy: String,
        @Query("searchIn") searchIn: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("domains") domains: String,
        @Query("language") language: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("excludeDomains") excludeDomains: String
    ): NewsModel

    companion object {
        private const val key = "c8556aedf85d4a5b80ad98ac35763a7a"
        private const val BASE_URL = "https://newsapi.org/v2/"

        fun create(): RetrofitService {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()

                    val requestBuilder = original.newBuilder()
                        .addHeader("X-Api-Key", key)
                        .method(original.method, original.body)

                    val request = requestBuilder.build()

                    chain.proceed(request)
                }
                .retryOnConnectionFailure(true)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitService::class.java)

        }


    }
}