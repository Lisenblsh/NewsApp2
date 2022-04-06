package com.example.newsapp2.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp2.data.network.Filter
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.databinding.FragmentMainBinding
import com.example.newsapp2.di.Injection
import com.example.newsapp2.ui.adapters.NewsAdapter
import com.example.newsapp2.ui.viewModel.NewsViewModel
import com.example.newsapp2.ui.viewModel.UiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
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
            pagingData = viewModel.pagingDataFlow
        )

        return binding.root
    }

    private fun FragmentMainBinding.bindState(pagingData: Flow<PagingData<ArticlesDB>>) {
        val newsAdapter = NewsAdapter()
        newsList.adapter = newsAdapter
        newsList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        bindList(
            pagingData = pagingData,
            newsAdapter = newsAdapter
        )
    }

    private fun FragmentMainBinding.bindList(
        pagingData: Flow<PagingData<ArticlesDB>>,
        newsAdapter: NewsAdapter
    ) {
        lifecycleScope.launch {
            Log.e("news", "asa")

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
