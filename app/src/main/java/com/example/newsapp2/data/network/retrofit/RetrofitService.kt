package com.example.newsapp2.data.network.retrofit

import com.example.newsapp2.BuildConfig
import com.example.newsapp2.data.network.*
import com.example.newsapp2.data.network.apiModels.NewsApiModel
import com.example.newsapp2.data.network.apiModels.NewsBingModel
import com.example.newsapp2.data.network.apiModels.NewscatcherModel
import com.example.newsapp2.data.network.apiModels.StopGameModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {
    @GET("everything?")
    suspend fun getNewsApiResponse(
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
    ): NewsApiModel

    @GET("search?originalImg=true")
    suspend fun getBingNewsResponse(
        @Query("q") q: String,
        @Query("count") count: Int,
        @Query("offset") offset: Int,
        @Query("setLang") setLang: String,
        @Query("sortBy") sortBy: String,
        @Query("safeSearch") safeSearch: SafeSearch,
        @Query("freshness") freshness: Freshness
    ): NewsBingModel

    @GET("search_free?media=true")
    suspend fun getNewscatcherResponse(
        @Query("q") q: String,
        @Query("lang") lang: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int
    ): NewscatcherModel

    @GET("api.json")
    suspend fun getStopGameResponse(@Query("rss_url") rssUrl: String): StopGameModel

    companion object {
        private const val BASE_URL_NEWSAPI = "https://newsapi.org/v2/"
        private const val BASE_URL_BING = "https://bing-news-search1.p.rapidapi.com/news/"
        private const val BASE_URL_NEWSCATHER = "https://newscatcher.p.rapidapi.com/v1/"
        private const val BASE_URL_STOPGAME = "https://api.rss2json.com/v1/"

        fun create(typeNewsUrl: TypeNewsUrl): RetrofitService {
            var headerName = ""
            var apiKey = ""
            val baseUrl = when (typeNewsUrl) {
                TypeNewsUrl.NewsApi -> {
                    headerName = "X-Api-Key"
                    apiKey = BuildConfig.API_KEY_NEWSAPI
                    BASE_URL_NEWSAPI
                }
                TypeNewsUrl.BingNews -> {
                    headerName = "X-RapidAPI-Key"
                    apiKey = BuildConfig.API_KEY_BING
                    BASE_URL_BING
                }
                TypeNewsUrl.Newscatcher -> {
                    headerName = "X-RapidAPI-Key"
                    apiKey = BuildConfig.API_KEY_BING
                    BASE_URL_NEWSCATHER
                }
                TypeNewsUrl.StopGame -> {
                    headerName = "X-Api-Key"
                    BASE_URL_STOPGAME
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