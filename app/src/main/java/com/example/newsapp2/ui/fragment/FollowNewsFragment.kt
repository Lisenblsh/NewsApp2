package com.example.newsapp2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp2.R
import com.example.newsapp2.data.network.TypeNewsUrl
import com.example.newsapp2.databinding.FragmentFollowNewsBinding
import com.example.newsapp2.databinding.FragmentRegularNewsBinding
import com.example.newsapp2.di.Injection
import com.example.newsapp2.tools.showWebView
import com.example.newsapp2.ui.adapters.NewsLoadStateAdapter
import com.example.newsapp2.ui.adapters.NewsPagingAdapter
import com.example.newsapp2.ui.viewModel.NewsViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException

class FollowNewsFragment : Fragment() {
    private lateinit var binding: FragmentFollowNewsBinding
    private lateinit var viewModel: NewsViewModel

    private val newsAdapter = NewsPagingAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized) {
            binding = FragmentFollowNewsBinding.inflate(inflater, container, false)
            viewModel = ViewModelProvider(
                this, Injection.provideViewModelFactory(
                    requireActivity().applicationContext, this, TypeNewsUrl.NewsApi
                )
            ).get(NewsViewModel::class.java)
        }
        binding.bindingElement()

        return binding.root
    }

    private fun FragmentFollowNewsBinding.bindingElement() {
        bindAdapter()
        initSwipeRefresh()
        initGoToUpBtn()
    }

    private fun FragmentFollowNewsBinding.bindAdapter() {
        val header = NewsLoadStateAdapter { newsAdapter.retry() }
        newsAdapter.setOnItemClickListener(object : NewsPagingAdapter.OnItemClickListener {
            override fun onItemClick(id: Long?) {
                if (id != null) {
                    showWebView(this@FollowNewsFragment, id)
                }
            }
        })
        newsList.adapter = newsAdapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = NewsLoadStateAdapter { newsAdapter.retry() }
        )
        newsList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        lifecycleScope.launch {
            viewModel.pagingDataFavoriteNewsFlow.collectLatest(newsAdapter::submitData)
        }

        lifecycleScope.launch {
            newsAdapter.loadStateFlow.collect { loadState ->
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && newsAdapter.itemCount >= 0 }
                    ?: loadState.prepend

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                    ?: loadState.refresh as? LoadState.Error

                errorState?.let {
                    if ((it.error as? HttpException)?.code() != 426) {
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.error_occurred, it.error.localizedMessage),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }

    private fun FragmentFollowNewsBinding.initSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            newsAdapter.refresh()
            swipeRefresh.isRefreshing = false
        }
    }

    private fun FragmentFollowNewsBinding.initGoToUpBtn() {
        goToUpButton.setOnClickListener {
            val position = (newsList.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            if (position >= 10) newsList.scrollToPosition(10)
            newsList.smoothScrollToPosition(0)
        }
    }
}
