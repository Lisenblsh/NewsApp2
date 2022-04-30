package com.example.newsapp2.data.network.apiModels

data class NewsdataModel(
    val results: List<NewsdataArticle>
)

data class NewsdataArticle(
    val description: String,
    val image_url: String?,
    val link: String,
    val pubDate: String,
    val title: String,
)