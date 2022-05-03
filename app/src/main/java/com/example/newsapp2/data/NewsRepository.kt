package com.example.newsapp2.data

import com.example.newsapp2.data.network.*
import com.example.newsapp2.data.network.retrofit.RetrofitService

class NewsRepository(private val retrofitService: RetrofitService) {

    suspend fun getNewsApiResponse(filter: FilterForNewsApi) = retrofitService.getNewsApiResponse(
        filter.query,
        filter.sortBy,
        filter.searchIn,
        filter.from,
        filter.to,
        filter.domains,
        filter.language,
        filter.page,
        filter.pageSize,
        filter.excludeDomains
    )

    suspend fun getBingNewsResponse(filter: FilterForBingNews) =
        retrofitService.getBingNewsResponse(
            filter.query,
            filter.count,
            filter.offset,
            filter.language,
            filter.sortBy,
            filter.safeSearch,
            filter.freshness
        )

    suspend fun getNewscatcherResponse(filter: FilterForNewscather) =
        retrofitService.getNewscatcherResponse(
            filter.query,
            filter.language,
            filter.page,
            filter.pageSize
        )

    suspend fun getStopGameResponse(filter: FilterForStopGame) =
        retrofitService.getStopGameResponse(
            filter.path
        )

    suspend fun getNewsdataResponse(filter: FilterForNewsData) =
        retrofitService.getNewsdataResponse(
            filter.language,
            filter.category,
            filter.page
        )

    suspend fun getWebSearchResponse(filter: FilterForWebSearch) =
        retrofitService.getWebSearchResponse(
            filter.page,
            filter.pageSize
        )
}