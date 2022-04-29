package com.example.newsapp2.data.network.apiModels

data class NewsBingModel(
    val value: List<NewsBingArticle>
)

data class NewsBingArticle(
    val datePublished: String,
    val description: String,
    val image: Image?,
    val name: String,
    val url: String
)

data class Image(
    val contentUrl: String,
)