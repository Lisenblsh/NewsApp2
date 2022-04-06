package com.example.newsapp2.data

import androidx.core.net.toUri
import com.example.newsapp2.data.network.Articles
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.data.room.TypeArticles

internal fun Articles.toArticlesDto(typeArticles: TypeArticles): ArticlesDB {
    return ArticlesDB(
        0,
        getDomain(url),
        author,
        title,
        description,
        url,
        urlToImage,
        publishedAt,
        typeArticles
    )
}//Преобразования данных из модели API в модель БД

internal fun ArticlesDB.toArticles(): Articles {
    return Articles(
        author,
        title,
        description,
        url,
        urlToImage,
        publishedAt
    )
}//Преобразования данных из модели БД в модель API

private fun getDomain(url: String): String {
    var domain = "${url.toUri().host}"
    if (domain.subSequence(0, 4) == "www.") {
        domain = domain.subSequence(4, domain.length).toString()
    }
    return domain
}