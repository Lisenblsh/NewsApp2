package com.example.newsapp2.data.network

data class Filter(
    val newsLanguage: String = "",
    val newsQuery: String = "q",
    val newsSortBy: String = "publishedAt",
    val searchIn: String = "",
    val newsFrom: String = "",
    val newsTo: String = "",
    val page: Int = 1,
    val pageSize: Int = 10,
    val excludeDomains: String = "",
    val newsDomains: String = ""
)

object CurrentFilter{
    var filterForNews = Filter()
    var newsDomains = ""
    var filterForFavorite = Filter(newsDomains = newsDomains)
}