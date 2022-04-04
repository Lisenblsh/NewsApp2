package com.example.newsapp2.data.network.retrofit

import com.example.newsapp2.data.network.Articles
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {
    @GET("{typeNews}?")
    suspend fun getNews(
        @Path("typeNews") typeNews: String = "everything",
        @Query("q") q: String = "a",
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("searchIn") searchIn: String ="",
        @Query("from") from: String = "",
        @Query("to") to: String = "",
        @Query("domains") domains: String = "",
        @Query("language") language: String = "",
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 10,
        @Query("excludeDomains") excludeDomains: String = ""
    ): Response<List<Articles>>
}