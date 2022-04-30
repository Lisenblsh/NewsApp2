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
    val sortBy: String = "Date", //Date/Relevance
    val safeSearch: SafeSearch = SafeSearch.Off,
    val freshness: Freshness = Freshness.Day
)

enum class Freshness {
    Day, Week, Month
}

enum class SafeSearch {
    Off, Moderate, Strict
}

data class FilterForNewscather(
    val query: String = "a",
    val language: String = "",
    val page: Int = 1,
    val pageSize: Int = 10
)

data class FilterForStopGame(
    val path: String = "https://rss.stopgame.ru/rss_news.xml" //rss_news.xml
)

data class FilterForNewsData(
    val language: String = "en",
    val category: Category = Category.Top,
    val page: Int = 1
)

enum class Category {
    Top, Business, Science, Technology, Sports, Health, Entertainment
}

data class FilterForWebSearch(
    val page: Int = 1,
    val pageSize: Int = 10
)

object CurrentFilter {
    var excludeDomains = ""
    var filterForNewsApi = FilterForNewsApi()
    var newsDomains = ""
    var filterForFavoriteNewsApi = FilterForNewsApi(domains = newsDomains)
    var filterForBingNews = FilterForBingNews()
    var filterForNewscatcher = FilterForNewscather()
    var filterForStopGame = FilterForStopGame()
    var filterForNewsData = FilterForNewsData()
    var filterForWebSearch = FilterForWebSearch()
}