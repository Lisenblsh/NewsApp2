package com.example.newsapp2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp2.data.room.NewsDataBase
import com.example.newsapp2.databinding.FragmentHomeBinding
import com.example.newsapp2.tools.DatabaseFun
import com.example.newsapp2.tools.showWebView
import com.example.newsapp2.ui.adapters.FavoriteNewsAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private val newsAdapter = FavoriteNewsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.bindAdapter()
        return binding.root
    }

    private suspend fun addElementToAdapter() {
        val databaseFun = DatabaseFun(NewsDataBase.getInstance(requireContext()))
        newsAdapter.submitList(databaseFun.getLikedArticlesList().reversed())
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