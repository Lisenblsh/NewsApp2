package com.example.newsapp2.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp2.R
import com.example.newsapp2.data.room.NewsDataBase
import com.example.newsapp2.databinding.FragmentHomeBinding
import com.example.newsapp2.databinding.FragmentRegularNewsBinding
import com.example.newsapp2.tools.getLikedArticlesList
import com.example.newsapp2.tools.showWebView
import com.example.newsapp2.ui.adapters.FavoriteNewsAdapter
import com.example.newsapp2.ui.adapters.NewsPagingAdapter
import com.example.newsapp2.ui.adapters.NewsLoadStateAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private val newsAdapter = FavoriteNewsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =  FragmentHomeBinding.inflate(inflater, container, false)
        binding.bindAdapter()
        return binding.root
    }

    private suspend fun addElementToAdapter(){
        newsAdapter.submitList(getLikedArticlesList(NewsDataBase.getInstance(requireContext())).reversed())
    }

    private fun FragmentHomeBinding.bindAdapter() {
        newsAdapter.setOnItemClickListener(object : FavoriteNewsAdapter.OnItemClickListener {
            override fun onItemClick(id: Long?) {
                if (id != null && id != -1L) {
                    showWebView(this@HomeFragment, id)
                }
            }
        })
        newsList.adapter = newsAdapter
        newsList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        lifecycleScope.launch {
            addElementToAdapter()
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            addElementToAdapter()
        }
    }

}