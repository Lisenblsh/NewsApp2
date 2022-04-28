package com.example.newsapp2.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp2.R
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.tools.convertToDeviceDate
import com.squareup.picasso.Picasso

class NewsPagingAdapter : PagingDataAdapter<ArticlesDB, RecyclerView.ViewHolder>(ARTICLES_COMPARATOR) {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val news = getItem(position)
        (holder as NewsViewHolder).bind(news)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_card_layout, parent, false)
        return NewsViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.news_card_layout
    }

    companion object {
        private val ARTICLES_COMPARATOR = object : DiffUtil.ItemCallback<ArticlesDB>() {
            override fun areItemsTheSame(oldItem: ArticlesDB, newItem: ArticlesDB): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: ArticlesDB, newItem: ArticlesDB): Boolean {
                return oldItem == newItem
            }

        }
    }

    interface OnItemClickListener {
        fun onItemClick(id: Long?)
    }

    private lateinit var clickListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.clickListener = listener
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.news_title)
        private val description: TextView = itemView.findViewById(R.id.news_description)
        private val newsDate: TextView = itemView.findViewById(R.id.news_date)
        private val source: TextView? = itemView.findViewById(R.id.news_source)
        private val image: ImageView = itemView.findViewById(R.id.news_image)

        private var news: ArticlesDB? = null

        init {
            itemView.setOnClickListener {
                val id = news?.idArticles
                clickListener.onItemClick(id)
            }
        }

        fun bind(news: ArticlesDB?) {
            if (news == null) {
                val resource = itemView.resources
                title.text = resource.getString(R.string.loading)
                description.visibility = View.GONE
                newsDate.text = resource.getString(R.string.loading)
                source?.text = resource.getString(R.string.loading)
                image.visibility = View.GONE
            } else {
                showRepoData(news)
            }
        }

        private fun showRepoData(news: ArticlesDB) {
            this.news = news
            title.text = news.title

            var descriptionVisibility = View.GONE
            if (news.description != null) {
                description.text = news.description
                descriptionVisibility = View.VISIBLE
            }
            description.visibility = descriptionVisibility

            newsDate.text = convertToDeviceDate(news.publishedAt)
            source?.text = news.source

            var imageVisibility = View.GONE
            if (news.urlToImage != null) {
                setImage(news.urlToImage)
                imageVisibility = View.VISIBLE
            }
            image.visibility = imageVisibility
        }

        private fun setImage(url: String) {
            Picasso.get()
                .load(url)
                .into(image)

        }
    }
}

