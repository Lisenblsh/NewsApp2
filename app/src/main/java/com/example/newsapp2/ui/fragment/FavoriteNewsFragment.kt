package com.example.newsapp2.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.databinding.FragmentFavoriteNewsBinding
import com.example.newsapp2.di.Injection
import com.example.newsapp2.ui.adapters.NewsAdapter
import com.example.newsapp2.ui.viewModel.NewsViewModel
import com.example.newsapp2.ui.viewModel.UiAction
import com.example.newsapp2.ui.viewModel.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteNewsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFavoriteNewsBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(
            this, Injection.provideViewModelFactory(
                requireActivity().applicationContext, this
            )
        ).get(NewsViewModel::class.java)

        binding.bindState(
            uiState = viewModel.stateFav,
            pagingData = viewModel.pagingDataFavoriteNewsFlow,
            uiActions = viewModel.accept
        )

        return binding.root
    }

    private fun FragmentFavoriteNewsBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<ArticlesDB>>,
        uiActions: (UiAction) -> Unit
    ) {
        val newsAdapter = NewsAdapter()
        newsAdapter.setOnItemClickListener(object : NewsAdapter.OnItemClickListener {
            override fun onItemClick(itemView: View?, url: String?) {

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                itemView?.context?.startActivity(intent)
            }

        })
        newsList.adapter = newsAdapter
        newsList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        newsList.scrollToPosition(0)
        bindList(
            uiState = uiState,
            pagingData = pagingData,
            newsAdapter = newsAdapter,
            onScrollChanged = uiActions

        )
    }

    private fun FragmentFavoriteNewsBinding.bindList(
        pagingData: Flow<PagingData<ArticlesDB>>,
        newsAdapter: NewsAdapter,
        onScrollChanged: (UiAction) -> Unit,
        uiState: StateFlow<UiState>
    ) {
        newsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentFavoriteFiler = uiState.value.lastFavoriteFilterScrolled))
            }
        })
        lifecycleScope.launch {
            pagingData.collectLatest(newsAdapter::submitData)
        }
    }
}