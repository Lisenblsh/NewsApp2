package com.example.newsapp2.data.network.retrofit

import com.example.newsapp2.data.network.Articles
import com.example.newsapp2.data.network.NewsModel
import retrofit2.Response
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
    ): Response<NewsModel>
}