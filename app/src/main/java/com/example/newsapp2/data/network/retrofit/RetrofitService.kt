package com.example.newsapp2.data.network.retrofit

import com.example.newsapp2.data.network.Category
import com.example.newsapp2.data.network.Freshness
import com.example.newsapp2.data.network.SafeSearch
import com.example.newsapp2.data.network.TypeNewsUrl
import com.example.newsapp2.data.room.ArticlesDB
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {

    @GET("{apiName}?")
    suspend fun getNewsLisApi(
        @Path("apiName") apiName: TypeNewsUrl,
        @Query("q") q: String,
        @Query("lang") lang: String,
        @Query("sortBy") sortBy: String,
        @Query("searchIn") searchIn: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("domains") domains: String,
        @Query("excludeDomains") excludeDomains: String,
        @Query("safeSearch") safeSearch: SafeSearch,
        @Query("freshness") freshness: Freshness,
        @Query("category") category: Category,
        @Query("rssFeed") rssFeed: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ):List<ArticlesDB>

    companion object {
        private const val BASE_NEWS_LIS_API = "https://newslisapi.herokuapp.com/"

        fun create(): RetrofitService {
            val baseUrl = BASE_NEWS_LIS_API

            val okHttpClient = OkHttpClient.Builder()
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