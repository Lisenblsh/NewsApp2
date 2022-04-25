package com.example.newsapp2.data.network.retrofit

import com.example.newsapp2.BuildConfig
import com.example.newsapp2.data.network.NewsModel
import com.example.newsapp2.data.network.NewsUrlType
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.internal.addHeaderLenient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
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
        private const val BASE_URL_NEWSAPI = "https://newsapi.org/v2/"
        private const val BASE_URL_BING = "https://bing-news-search1.p.rapidapi.com/news/search/"

        fun create(urlType: NewsUrlType): RetrofitService {

            var headerName = ""
            var apiKey = ""
            val baseUrl = when(urlType){
                NewsUrlType.NewsApi -> {
                    headerName = "X-Api-Key"
                    apiKey = BuildConfig.API_KEY_NEWSAPI
                    BASE_URL_NEWSAPI
                }
                NewsUrlType.BingNews -> {
                    headerName = "X-RapidAPI-Key"
                    apiKey = BuildConfig.API_KEY_BING
                    BASE_URL_BING
                }
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()

                    val requestBuilder = original.newBuilder()
                        .addHeader(headerName, apiKey)
                        .method(original.method, original.body)

                    val request = requestBuilder.build()

                    chain.proceed(request)
                }
                .retryOnConnectionFailure(true)
                .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitService::class.java)
        }
    }
}