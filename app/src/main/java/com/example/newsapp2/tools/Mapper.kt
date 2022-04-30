package com.example.newsapp2.tools

import com.example.newsapp2.data.network.TypeNewsUrl
import com.example.newsapp2.data.network.apiModels.*
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.data.room.TypeArticles
import java.net.URL

class Mapper(private val typeNewsUrl: TypeNewsUrl, private val typeArticles: TypeArticles = TypeArticles.RegularNews) {
    fun mapNewscatcherToDB(article: NewscatcherArticle) =
        ArticlesDB(
            0,
            article.clean_url,
            article.title,
            article.summary,
            article.link,
            article.media,
            convertDateToMillis(article.published_date, typeNewsUrl),
            typeArticles,
            typeNewsUrl
        )

    fun mapBingNewsToDB(article: NewsBingArticle) =
        ArticlesDB(
            0,
            getDomainFromUrl(article.url),
            article.name,
            article.description,
            article.url,
            article.image?.contentUrl,
            convertDateToMillis(article.datePublished, typeNewsUrl),
            typeArticles,
            typeNewsUrl
        )

    fun mapNewsApiToDB(article: NewsApiArticle) =
        ArticlesDB(
            0,
            getDomainFromUrl(article.url),
            article.title,
            article.description,
            article.url,
            article.urlToImage,
            convertDateToMillis(article.publishedAt, typeNewsUrl),
            typeArticles,
            typeNewsUrl
        )

    fun mapStopGameToDB(article: StopGameArticle) = ArticlesDB(
        0,
        "stopgame.ru",
        article.title,
        article.description,
        article.link,
        article.enclosure.link,
        convertDateToMillis(article.pubDate, typeNewsUrl),
        typeArticles,
        typeNewsUrl
    )

    fun mapNewsDataToDB(article: NewsdataArticle) = ArticlesDB(
        0,
        getDomainFromUrl(article.link),
        article.title,
        article.description,
        article.link,
        article.image_url,
        convertDateToMillis(article.pubDate, typeNewsUrl),
        typeArticles,
        typeNewsUrl
    )

    fun mapWebSearchToDB(article: WebSearchArticle) = ArticlesDB(
        0,
        getDomainFromUrl(article.url),
        article.title,
        article.description,
        article.url,
        article.image.url,
        convertDateToMillis(article.datePublished, typeNewsUrl),
        typeArticles,
        typeNewsUrl
    )

    private fun getDomainFromUrl(url: String): String {
        var domain = URL(url).host
        if (domain.subSequence(0, 4) == "www.") {
            domain = domain.subSequence(4, domain.length).toString()
        }
        return domain
    }

}