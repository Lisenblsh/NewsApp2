package com.example.newsapp2.tools

import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.room.withTransaction
import com.example.newsapp2.data.room.*
import com.example.newsapp2.ui.fragment.MainFragmentDirections

class LogicForWebView(private val dataBase: NewsDataBase, private val articleId: Long) {
    private lateinit var article: ArticlesDB
    private lateinit var domain: String

    suspend fun getUrl(): String {
        return try {
            article = dataBase.newsListDao().getArticlesData(articleId)
                .copy(idArticles = 0, typeArticles = TypeArticles.LikedNews)
            domain = "${article.url.toUri().host}"
            if (domain.subSequence(0, 4) == "www.") {
                domain = domain.subSequence(4, domain.length).toString()
            }
            article.url
        } catch (e: Exception) {
            ""
        }
    }

    fun getTextForMessage(): String {
        var text = if (article.title != null) "\"${article.title}\"\n" else ""
        text += article.url
        return text
    }

    suspend fun isLikedNews(): Boolean {
        return dataBase.withTransaction {
            dataBase.newsListDao().getArticlesData(
                article.source,
                article.url,
                article.publishedAt,
                article.typeArticles
            ) != null
        }
    }

    suspend fun likeNews() {
        dataBase.newsListDao().insertArticle(article)
    }

    suspend fun unlikeNews() {
        dataBase.newsListDao().deleteLikedArticle(
            article.source,
            article.url,
            article.publishedAt,
            article.typeArticles
        )
    }

    suspend fun saveSource(type: TypeSource) {
        dataBase.newsListDao().insertSources(SourcesDB(0, domain, type))
    }

    suspend fun removeSource(type: TypeSource) {
        dataBase.newsListDao().deleteSources(domain, type)
    }

    suspend fun isFollowedSource(): Boolean {
        return dataBase.withTransaction {
            dataBase.newsListDao().getSourcesData(domain, TypeSource.FollowSource) != null
        }
    }

    suspend fun isBlockedSource(): Boolean {
        return dataBase.withTransaction {
            dataBase.newsListDao().getSourcesData(domain, TypeSource.BlockSource) != null
        }
    }

    fun getDomain() = domain
}

fun showWebView(fragment: Fragment, articlesId: Long) {
    val action = MainFragmentDirections.actionMainFragmentToNewsWebViewFragment(
        articlesId
    )
    val navController = NavHostFragment.findNavController(fragment)
    navController.navigate(action)
}//Показать выбранную новость в WebView