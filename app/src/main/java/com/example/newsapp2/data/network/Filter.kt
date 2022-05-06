package com.example.newsapp2.data.network

data class Filter(
    val apiName: TypeNewsUrl = TypeNewsUrl.NewsApi,
    val q: String = "",
    val lang: String = "",
    val sortBy: String = "",
    val searchIn: String = "",
    val from: String = "",
    val to: String = "",
    val domains: String = "",
    val excludeDomains: String = "",
    val safeSearch: SafeSearch = SafeSearch.Off,
    val freshness: Freshness = Freshness.Day,
    val category: Category = Category.Top,
    val rssFeed: String = "",
    val page: Int = 1,
    val pageSize: Int = 10,
)

enum class Freshness {
    Day, Week, Month
}

enum class SafeSearch {
    Off, Moderate, Strict
}

enum class Category {
    Top, Business, Science, Technology, Sports, Health, Entertainment
}

object CurrentFilter {
    var filter = Filter()
    var filterFav = Filter()
}