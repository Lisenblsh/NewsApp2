package com.example.newsapp2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp2.data.network.CurrentFilter
import com.example.newsapp2.data.network.Filter
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.databinding.FragmentMainBinding
import com.example.newsapp2.di.Injection
import com.example.newsapp2.ui.adapters.NewsAdapter
import com.example.newsapp2.ui.viewModel.NewsViewModel
import com.example.newsapp2.ui.viewModel.UiAction
import com.example.newsapp2.ui.viewModel.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsFilter: Filter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        val viewModel = ViewModelProvider(
            this, Injection.provideViewModelFactory(
                requireActivity().applicationContext, this
            )
        ).get(NewsViewModel::class.java)

        binding.bindState(
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            uiActions = viewModel.accept
        )

        return binding.root
    }

    private fun FragmentMainBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<ArticlesDB>>,
        uiActions: (UiAction) -> Unit
    ) {
        val newsAdapter = NewsAdapter()
        newsList.adapter = newsAdapter
        newsList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        bindList(
            uiState = uiState,
            pagingData = pagingData,
            newsAdapter = newsAdapter,
            onScrollChanged = uiActions

        )
    }

    private fun FragmentMainBinding.bindList(
        pagingData: Flow<PagingData<ArticlesDB>>,
        newsAdapter: NewsAdapter,
        onScrollChanged: (UiAction) -> Unit,
        uiState: StateFlow<UiState>
    ) {
        newsList.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(dy != 0) onScrollChanged(UiAction.Scroll(currentFilter = uiState.value.lastQueryScrolled))
            }
        })
        lifecycleScope.launch {
            pagingData.collectLatest(newsAdapter::submitData)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
