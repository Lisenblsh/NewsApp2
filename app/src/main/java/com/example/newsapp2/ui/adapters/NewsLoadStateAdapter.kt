package com.example.newsapp2.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp2.R
import com.example.newsapp2.databinding.NewsLoadStateBinding
import retrofit2.HttpException

class NewsLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<NewsLoadStateAdapter.NewsLoadStateViewHolder>() {

    override fun onBindViewHolder(holder: NewsLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): NewsLoadStateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_load_state, parent, false)
        val binding = NewsLoadStateBinding.bind(view)
        return NewsLoadStateViewHolder(binding, retry)
    }

    inner class NewsLoadStateViewHolder(
        private val binding: NewsLoadStateBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            val code =
                if (loadState is LoadState.Error) (loadState.error as? HttpException)?.code() else 0
            if (loadState is LoadState.Error) {
                binding.errorMsg.text =
                    if (code == 426) "Вы достигли конца" else loadState.error.localizedMessage
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            if (code != 426) {
                binding.retryButton.isVisible = loadState is LoadState.Error
            }
            binding.errorMsg.isVisible = loadState is LoadState.Error
        }
    }
}