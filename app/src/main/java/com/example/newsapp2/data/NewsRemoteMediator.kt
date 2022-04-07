package com.example.newsapp2.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.newsapp2.data.network.CurrentFilter
import com.example.newsapp2.data.network.Filter
import com.example.newsapp2.data.network.retrofit.RetrofitService
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.data.room.NewsDataBase
import com.example.newsapp2.data.room.RemoteKeys
import com.example.newsapp2.data.room.TypeArticles
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1


@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val retrofitService: RetrofitService,
    private val newsDataBase: NewsDataBase,
    private val typeArticles: TypeArticles
) : RemoteMediator<Int, ArticlesDB>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticlesDB>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1)?: STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            CurrentFilter.filterForNews = Filter(page = page, pageSize = state.config.pageSize)
            val apiResponse = getNews(CurrentFilter.filterForNews)

            val news = apiResponse.articles.map { it.toArticlesDto(typeArticles) }
            val endOfPaginationReached = news.isEmpty()
            newsDataBase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    newsDataBase.newsListDao().clearArticles(typeArticles)
                    newsDataBase.newsListDao().clearRemoteKeys(typeArticles)
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                news.forEach {
                    Log.e("nes","${it.idArticles}")

                }
                newsDataBase.newsListDao().insertAllArticles(news)
                val keys = newsDataBase.newsListDao().getArticlesData2(typeArticles).map {
                    RemoteKeys(newsId = it.idArticles, prevKey = prevKey, nextKey = nextKey, typeArticles)
                }
                Log.e("nes","${keys.size}")
                newsDataBase.newsListDao().insertAllKeys(keys)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getNews(filter: Filter) = retrofitService.getNews(
        filter.newsQuery,
        filter.newsSortBy,
        filter.searchIn,
        filter.newsFrom,
        filter.newsTo,
        filter.newsDomains,
        filter.newsLanguage,
        filter.page,
        filter.pageSize,
        filter.excludeDomains
    )

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ArticlesDB>): RemoteKeys? {
        return state.pages.firstOrNull() { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo ->
                newsDataBase.newsListDao().remoteKeysNewsId(repo.idArticles)
            }
    }
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ArticlesDB>): RemoteKeys? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo ->
                newsDataBase.newsListDao().remoteKeysNewsId(repo.idArticles)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ArticlesDB>
    ): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.idArticles?.let { newsId ->
                newsDataBase.newsListDao().remoteKeysNewsId(newsId)
            }
        }
    }
}