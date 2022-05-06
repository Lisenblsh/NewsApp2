package com.example.newsapp2.data

import com.example.newsapp2.data.network.Filter
import com.example.newsapp2.data.network.retrofit.RetrofitService

class NewsRepository(private val retrofitService: RetrofitService) {
    suspend fun getNewsLisApi(filter: Filter) = retrofitService.getNewsLisApi(
        apiName = filter.apiName,
        q = filter.q,
        lang = filter.lang,
        sortBy = filter.sortBy,
        searchIn = filter.searchIn,
        from = filter.from,
        to = filter.to,
        domains = filter.domains,
        excludeDomains = filter.excludeDomains,
        safeSearch = filter.safeSearch,
        freshness = filter.freshness,
        category = filter.category,
        rssFeed = filter.rssFeed,
        page = filter.page,
        pageSize = filter.pageSize
    )
}