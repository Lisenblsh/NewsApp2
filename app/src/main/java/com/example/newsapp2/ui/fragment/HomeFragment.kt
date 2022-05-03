package com.example.newsapp2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
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

    private lateinit var databaseFun: DatabaseFun

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        databaseFun = DatabaseFun(NewsDataBase.getInstance(requireContext()))
        binding.bindAdapter()
        return binding.root
    }


    private suspend fun addElementToAdapter() {
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
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        val deletedNews = newsAdapter.currentList[position]
                        lifecycleScope.launch {
                            databaseFun.deleteLikedArticle(deletedNews)
                            addElementToAdapter()
                        }
                    }
                }
            }
        }
        ).attachToRecyclerView(newsList)
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