package com.example.newsapp2.data.network

data class FilterForNewsApi(
    val language: String = "",
    val query: String = "a",
    val sortBy: String = "publishedAt",
    val searchIn: String = "",
    val from: String = "",
    val to: String = "",
    val page: Int = 1,
    val pageSize: Int = 10,
    val excludeDomains: String = "",
    val domains: String = "",
)

data class FilterForBingNews(
    val query: String = "",
    val count: Int = 10,
    val offset: Int = 0,
    val language: String = "",
    val sortBy: String = "Date" //Date/Relevance
)

data class FilterForNewscather(
    val query: String = "a",
    val language: String = "",
    val page: Int = 1,
    val pageSize: Int = 10
)

object CurrentFilter{
    var excludeDomains = ""
    var filterForNewsApi = FilterForNewsApi()
    var newsDomains = ""
    var filterForFavoriteNewsApi = FilterForNewsApi(domains = newsDomains)
    var filterForBingNews = FilterForBingNews()
    var filterForNewscatcher = FilterForNewscather()
}