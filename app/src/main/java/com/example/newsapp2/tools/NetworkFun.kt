package com.example.newsapp2.tools

import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.newsapp2.data.room.NewsDataBase
import com.example.newsapp2.ui.fragment.MainFragmentDirections
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import okhttp3.internal.wait
import kotlin.coroutines.coroutineContext

class LogicForWebView(private val dataBase: NewsDataBase) {
    suspend fun getUrl(id: Long): String {
        return dataBase.newsListDao().getArticlesData(id).url
    }
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