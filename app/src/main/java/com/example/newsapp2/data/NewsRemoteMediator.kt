package com.example.newsapp2.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.newsapp2.data.network.CurrentFilter
import com.example.newsapp2.data.network.TypeNewsUrl
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.data.room.NewsDataBase
import com.example.newsapp2.data.room.RemoteKeys
import com.example.newsapp2.data.room.TypeArticles
import com.example.newsapp2.tools.DatabaseFun
import com.example.newsapp2.tools.Mapper
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

        Log.e("pageS", state.config.pageSize.toString())

        try {
            val (news, endOfPaginationReached) = getNewsList(page, state)

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
        state: PagingState<Int, ArticlesDB>
    ): Pair<List<ArticlesDB>, Boolean> {
        val mapper = Mapper(typeNewsUrl, typeArticles)
        val news = when (typeNewsUrl) {
            TypeNewsUrl.NewsApi -> {
                val dbFun = DatabaseFun(newsDataBase)
                val responseNewsApi = when (typeArticles) {
                    TypeArticles.RegularNews -> {
                        CurrentFilter.excludeDomains = dbFun.getExcludeDomains()
                        CurrentFilter.filterForNewsApi =
                            CurrentFilter.filterForNewsApi.copy(
                                page = page,
                                pageSize = state.config.pageSize,
                                excludeDomains = CurrentFilter.excludeDomains
                            )
                        repository.getNewsApiResponse(CurrentFilter.filterForNewsApi)
                    }
                    else -> {
                        CurrentFilter.newsDomains = dbFun.getNewsDomains()
                        CurrentFilter.filterForFavoriteNewsApi =
                            CurrentFilter.filterForFavoriteNewsApi.copy(
                                page = page,
                                pageSize = state.config.pageSize,
                                domains = CurrentFilter.newsDomains
                            )
                        repository.getNewsApiResponse(CurrentFilter.filterForFavoriteNewsApi)
                    }
                }
                responseNewsApi.articles.map {
                    mapper.mapNewsApiToDB(it)
                }
            }
            TypeNewsUrl.BingNews -> {
                CurrentFilter.filterForBingNews = CurrentFilter.filterForBingNews.copy(
                    offset = page * CurrentFilter.filterForBingNews.count
                )
                repository.getBingNewsResponse(CurrentFilter.filterForBingNews).value.map {
                    mapper.mapBingNewsToDB(it)
                }
            }
            TypeNewsUrl.Newscatcher -> {
                CurrentFilter.filterForNewscatcher = CurrentFilter.filterForNewscatcher.copy(
                    page = page,
                    pageSize = state.config.pageSize
                )
                repository.getNewscatcherResponse(CurrentFilter.filterForNewscatcher).articles.map {
                    mapper.mapNewscatcherToDB(it)
                }
            }
            TypeNewsUrl.StopGame -> {
                repository.getStopGameResponse(CurrentFilter.filterForStopGame).items.map {
                    mapper.mapStopGameToDB(it)
                }
            }
            TypeNewsUrl.NewsData -> {
                CurrentFilter.filterForNewsData = CurrentFilter.filterForNewsData.copy(
                    page = page
                )
                repository.getNewsdataResponse(CurrentFilter.filterForNewsData).results.map {
                    mapper.mapNewsDataToDB(it)
                }
            }
            TypeNewsUrl.WebSearch -> {
                CurrentFilter.filterForWebSearch = CurrentFilter.filterForWebSearch.copy(
                    page = page,
                    pageSize = state.config.pageSize
                )
                repository.getWebSearchResponse(CurrentFilter.filterForWebSearch).value.map {
                    mapper.mapWebSearchToDB(it)
                }
            }
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