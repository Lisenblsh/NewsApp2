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

private fun getDomain(url: String): String {
    var domain = "${url.toUri().host}"
    if (domain.subSequence(0, 4) == "www.") {
        domain = domain.subSequence(4, domain.length).toString()
    }
    return domain
}