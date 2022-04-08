package com.example.newsapp2.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.databinding.FragmentRegularNewsBinding
import com.example.newsapp2.di.Injection
import com.example.newsapp2.ui.adapters.NewsAdapter
import com.example.newsapp2.ui.adapters.NewsLoadStateAdapter
import com.example.newsapp2.ui.viewModel.NewsViewModel
import com.example.newsapp2.ui.viewModel.UiAction
import com.example.newsapp2.ui.viewModel.UiState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegularNewsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentRegularNewsBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(
            this, Injection.provideViewModelFactory(
                requireActivity().applicationContext, this
            )
        ).get(NewsViewModel::class.java)

        binding.bindState(
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataRegularNewsFlow,
            uiActions = viewModel.accept
        )

        return binding.root
    }

    private fun FragmentRegularNewsBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<ArticlesDB>>,
        uiActions: (UiAction) -> Unit
    ) {
        val newsAdapter = NewsAdapter()
        val header = NewsLoadStateAdapter { newsAdapter.retry() }
        newsList.adapter = newsAdapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = NewsLoadStateAdapter { newsAdapter.retry() }
        )
        newsAdapter.setOnItemClickListener(object : NewsAdapter.OnItemClickListener {
            override fun onItemClick(itemView: View?, url: String?) {

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                itemView?.context?.startActivity(intent)
            }

        })
        newsList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        newsList.scrollToPosition(0)
        bindList(
            uiState = uiState,
            header = header,
            pagingData = pagingData,
            newsAdapter = newsAdapter,
            onScrollChanged = uiActions

        )
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun FragmentRegularNewsBinding.bindList(
        pagingData: Flow<PagingData<ArticlesDB>>,
        newsAdapter: NewsAdapter,
        onScrollChanged: (UiAction) -> Unit,
        uiState: StateFlow<UiState>,
        header: NewsLoadStateAdapter
    ) {
        newsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentRegularFilter = uiState.value.lastRegularFilterScrolled))
            }
        })
        lifecycleScope.launch {
            pagingData.collectLatest(newsAdapter::submitData)
        }

        lifecycleScope.launch {
            newsAdapter.loadStateFlow.collect { loadState ->
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && newsAdapter.itemCount > 0 }
                    ?: loadState.prepend

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }

    }
}
