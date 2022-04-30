package com.example.newsapp2.data.network.apiModels

data class WebSearchModel(
    val value: List<WebSearchArticle>
)

data class WebSearchArticle(
    val datePublished: String,
    val description: String,
    val image: WebSearchImage,
    val title: String,
    val url: String
)

data class WebSearchImage(
    val url: String
)