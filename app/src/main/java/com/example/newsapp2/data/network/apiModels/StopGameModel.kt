package com.example.newsapp2.data.network.apiModels

data class StopGameModel(
    val items: List<StopGameArticle>
)

data class StopGameArticle (
    val title: String,
    val pubDate: String,
    val link: String,
    val description: String,
    val enclosure: Enclosure,
)

data class Enclosure (
    val link: String
)
