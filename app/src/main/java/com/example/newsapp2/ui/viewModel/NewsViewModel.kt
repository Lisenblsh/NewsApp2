package com.example.newsapp2.ui.viewModel

import androidx.lifecycle.*
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

    val pagingDataFlow: Flow<PagingData<ArticlesDB>>

    val accept: (UiAction) -> Unit

    init {
        pagingDataFlow = getCurrentNews().cachedIn(viewModelScope)
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val lastFilterScrolled = CurrentFilter.filterForNews
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
            .onStart { emit(UiAction.Scroll(currentFilter = lastFilterScrolled)) }

        state = filterScrolled.map {  scroll ->
            UiState(
                lastQueryScrolled = scroll.currentFilter
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
    data class Scroll(val currentFilter: Filter) : UiAction()
}

data class UiState(
    val lastQueryScrolled: Filter = Filter()
)