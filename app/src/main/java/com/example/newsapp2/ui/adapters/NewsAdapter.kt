package com.example.newsapp2.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp2.R
import com.example.newsapp2.data.room.ArticlesDB
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter : PagingDataAdapter<ArticlesDB, RecyclerView.ViewHolder>(ARTICLES_COMPARATOR) {
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
        fun onItemClick(itemView: View?, url: String?)
    }

    private lateinit var clickListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.clickListener = listener
    }

    inner class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = itemView.findViewById(R.id.news_title)
        private val description: TextView = itemView.findViewById(R.id.news_description)
        private val newsDate: TextView = itemView.findViewById(R.id.news_date)
        private val source: TextView? = itemView.findViewById(R.id.news_source)
        private val image: ImageView = itemView.findViewById(R.id.news_image)

        private var news: ArticlesDB? = null

        init {
            itemView.setOnClickListener {
                val url = getItem(layoutPosition)?.url
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(itemView, url)
                }
            }
        }

        fun bind(news: ArticlesDB?) {
            if (news == null) {
                title.text = "Loading.."
                description.visibility = View.GONE
                newsDate.text = "Loading.."
                source?.text = "Loading.."
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
                pastImage(news.urlToImage)
                imageVisibility = View.VISIBLE
            }
            image.visibility = imageVisibility
        }

        private fun pastImage(url: String) {
            Glide
                .with(itemView)
                .load(url)
                .into(image)
        }

        private fun convertToDeviceDate(date: String): String {
            val dateString = SimpleDateFormat("d MMMM, HH:mm", Locale("ru"))
            val defaultDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val currentTimeZone = GregorianCalendar().timeZone.rawOffset
            return try{
                dateString.format(Date(defaultDate.parse(date).time + currentTimeZone))
            }catch (e: Exception) {
                ""
            }
        }
    }
}

