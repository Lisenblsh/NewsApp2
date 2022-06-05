package com.example.newsapp2.data

import android.util.Log
import androidx.paging.*
import androidx.room.withTransaction
import com.example.newsapp2.data.network.CurrentFilter
import com.example.newsapp2.data.network.TypeNewsUrl
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.data.room.NewsDataBase
import com.example.newsapp2.data.room.RemoteKeys
import com.example.newsapp2.data.room.TypeArticles
import com.example.newsapp2.tools.DatabaseFun
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val repository: NewsRepository,
    private val newsDataBase: NewsDataBase,
    private val typeArticles: TypeArticles,
    private val typeNewsUrl: TypeNewsUrl
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
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val (news, endOfPaginationReached) = getNewsList(page, state.config.pageSize)

            newsDataBase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    newsDataBase.newsListDao().clearArticles(typeArticles, typeNewsUrl)
                    newsDataBase.newsListDao().clearRemoteKeys(typeArticles, typeNewsUrl)
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                newsDataBase.newsListDao().insertAllArticles(news)
                val keys =
                    newsDataBase.newsListDao().getArticlesData2(typeArticles, typeNewsUrl)
                        .takeLast(state.config.pageSize).map {
                            RemoteKeys(
                                articleId = it.idArticles,
                                prevKey = prevKey,
                                nextKey = nextKey,
                                typeArticles,
                                typeNewsUrl
                            )
                        }
                newsDataBase.newsListDao().insertAllKeys(keys)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getNewsList(
        page: Int,
        pageSize: Int
    ): Pair<List<ArticlesDB>, Boolean> {
        val dbFun = DatabaseFun(newsDataBase)

        val news = if (typeArticles == TypeArticles.RegularNews) {
            val excludeDomain =
                if (typeNewsUrl == TypeNewsUrl.NewsApi) dbFun.getExcludeDomains() else ""
            CurrentFilter.filter =
                CurrentFilter.filter.copy(
                    excludeDomains = excludeDomain,
                    page = page,
                    pageSize = pageSize
                )
            repository.getNewsLisApi(CurrentFilter.filter)
        } else {
            CurrentFilter.filterFav =
                CurrentFilter.filterFav.copy(
                    domains = dbFun.getNewsDomains(),
                    page = page,
                    pageSize = pageSize
                )
            repository.getNewsLisApi(CurrentFilter.filterFav).map { it.copy(typeArticles = TypeArticles.FollowNews) }
        }

        val endOfPaginationReached =
            if (typeNewsUrl == TypeNewsUrl.StopGame) true else news.isEmpty()
        return Pair(news, endOfPaginationReached)
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ArticlesDB>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo ->
                newsDataBase.newsListDao().remoteKeysNewsId(repo.idArticles)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ArticlesDB>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.idArticles?.let { newsId ->
                newsDataBase.newsListDao().remoteKeysNewsId(newsId)
            }
        }
    }
}