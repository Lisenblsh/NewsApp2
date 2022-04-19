package com.example.newsapp2.tools

import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.room.withTransaction
import com.example.newsapp2.data.room.*
import com.example.newsapp2.ui.fragment.MainFragmentDirections
import kotlinx.coroutines.flow.asFlow
import okhttp3.internal.wait

class LogicForWebView(private val dataBase: NewsDataBase, private val articleId: Long) {
    private lateinit var article: ArticlesDB
    private lateinit var domain: String

    suspend fun getUrl(): String {
        article = dataBase.newsListDao().getArticlesData(articleId)
            .copy(idArticles = 0, typeArticles = TypeArticles.LikedNews)
        domain = "${article.url.toUri().host}"
        if (domain.subSequence(0, 4) == "www.") {
            domain = domain.subSequence(4, domain.length).toString()
        }
        return article.url
    }

    suspend fun isLikedNews(): Boolean {
        dataBase.withTransaction {
            dataBase.newsListDao().getArticlesData2(TypeArticles.LikedNews).map {
                if (it.copy(idArticles = 0) == article) return@withTransaction true
            }
        }
        return false
    }

    suspend fun likeNews() {
        dataBase.newsListDao().insertArticle(article)
    }

    suspend fun unlikeNews() {
        dataBase.newsListDao().deleteLikedArticles(
            article.title,
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
    val navOptions: NavOptions = NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setRestoreState(true)
        .setPopUpTo(
            navController.graph.startDestinationId,
            inclusive = false,
            saveState = true
        ) // saveState
        .build()
    navController.navigate(action, navOptions)
}//Показать выбранную новость в WebView