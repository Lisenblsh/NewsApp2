package com.example.newsapp2.ui.viewModel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.savedstate.SavedStateRegistryOwner
import com.example.newsapp2.data.NewsRepository
import com.example.newsapp2.data.network.CurrentFilter
import com.example.newsapp2.data.network.Filter
import com.example.newsapp2.data.room.ArticlesDB
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val state: StateFlow<UiState>
    val stateFav: StateFlow<UiState>

    val pagingDataRegularNewsFlow: Flow<PagingData<ArticlesDB>>
    val pagingDataFavoriteNewsFlow: Flow<PagingData<ArticlesDB>>

    val accept: (UiAction) -> Unit

    init {
        pagingDataRegularNewsFlow = getCurrentNews().cachedIn(viewModelScope)
        pagingDataFavoriteNewsFlow = getFavoriteNews().cachedIn(viewModelScope)
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val lastRegularFilterScrolled = CurrentFilter.filterForNews
        val lastFavoriteFilterScrolled = CurrentFilter.filterForFavorite
        val filterScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            // This is shared to keep the flow "hot" while caching the last query scrolled,
            // otherwise each flatMapLatest invocation would lose the last query scrolled,
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart {
                emit(
                    UiAction.Scroll(
                        currentRegularFilter = lastRegularFilterScrolled,
                        lastFavoriteFilterScrolled
                    )
                )
            }

        state = filterScrolled.map { scroll ->
            UiState(
                lastFavoriteFilterScrolled = scroll.currentRegularFilter
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )

        stateFav = filterScrolled.map { scroll ->
            UiState(
                lastFavoriteFilterScrolled = scroll.currentRegularFilter
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    private fun getCurrentNews(): Flow<PagingData<ArticlesDB>> =
        newsRepository.getNews()

    private fun getFavoriteNews(): Flow<PagingData<ArticlesDB>> =
        newsRepository.getFavoriteNews()
}

class NewsViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val newsRepository: NewsRepository
) : AbstractSavedStateViewModelFactory(owner, null) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(newsRepository, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

sealed class UiModel {
    data class NewsItem(val news: ArticlesDB) : UiModel()
}

sealed class UiAction {
    data class Scroll(val currentRegularFilter: Filter = Filter(), val currentFavoriteFiler: Filter = Filter()) :
        UiAction()
}

data class UiState(
    val lastRegularFilterScrolled: Filter = Filter(),
    val lastFavoriteFilterScrolled: Filter = Filter(newsDomains = CurrentFilter.newsDomains)
)