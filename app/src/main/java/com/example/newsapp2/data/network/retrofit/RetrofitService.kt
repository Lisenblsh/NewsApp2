package com.example.newsapp2.data.network.retrofit

import com.example.newsapp2.BuildConfig
import com.example.newsapp2.data.network.*
import com.example.newsapp2.data.network.apiModels.*
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

    @GET("api.json?api_key=rqp90ruf9tdsrkfwcbkh6pvuvdsqqh6qi0kiicyr&count=30")
    suspend fun getStopGameResponse(@Query("rss_url") rssUrl: String): StopGameModel

    @GET("news?")
    suspend fun getNewsdataResponse(
        @Query("language") language: String,
        @Query("category") category: Category,
        @Query("page") page: Int
    ): NewsdataModel

    @GET("TrendingNewsAPI?")
    suspend fun getWebSearchResponse(
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int
    ) : WebSearchModel

    companion object {
        private const val BASE_URL_NEWSAPI = "https://newsapi.org/v2/"
        private const val BASE_URL_BING = "https://bing-news-search1.p.rapidapi.com/news/"
        private const val BASE_URL_NEWSCATHER = "https://newscatcher.p.rapidapi.com/v1/"
        private const val BASE_URL_STOPGAME = "https://api.rss2json.com/v1/"
        private const val BASE_URL_NEWSDATA = "https://newsdata2.p.rapidapi.com/"
        private const val BASE_URL_WEBSEARCH = "https://contextualwebsearch-websearch-v1.p.rapidapi.com/api/search/"

        fun create(typeNewsUrl: TypeNewsUrl): RetrofitService {
            var headerName = "X-RapidAPI-Key"
            var apiKey = BuildConfig.API_KEY_BING
            val baseUrl = when (typeNewsUrl) {
                TypeNewsUrl.NewsApi -> {
                    headerName = "X-Api-Key"
                    apiKey = BuildConfig.API_KEY_NEWSAPI
                    BASE_URL_NEWSAPI
                }
                TypeNewsUrl.BingNews -> {
                    BASE_URL_BING
                }
                TypeNewsUrl.Newscatcher -> {
                    BASE_URL_NEWSCATHER
                }
                TypeNewsUrl.StopGame -> {
                    BASE_URL_STOPGAME
                }
                TypeNewsUrl.NewsData -> {
                    BASE_URL_NEWSDATA
                }
                TypeNewsUrl.WebSearch -> {
                    BASE_URL_WEBSEARCH
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