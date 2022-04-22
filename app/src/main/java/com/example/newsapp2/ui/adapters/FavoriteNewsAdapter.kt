package com.example.newsapp2.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp2.R
import com.example.newsapp2.data.room.ArticlesDB
import com.example.newsapp2.tools.convertToDeviceDate

class FavoriteNewsAdapter :
    ListAdapter<ArticlesDB, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private val news = 1
    private val footer = 2

    interface OnItemClickListener {
        fun onItemClick(id: Long?)
    }

    private lateinit var clickListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.clickListener = listener
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ArticlesDB> =
            object : DiffUtil.ItemCallback<ArticlesDB>() {
                override fun areItemsTheSame(oldItem: ArticlesDB, newItem: ArticlesDB): Boolean {
                    return oldItem.title == newItem.title
                }

                override fun areContentsTheSame(oldItem: ArticlesDB, newItem: ArticlesDB): Boolean {
                    return oldItem == newItem
                }
            }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == currentList.size) footer else news
    }

    override fun getItemCount(): Int {
        return currentList.size + 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            news -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.small_card_layout, parent, false)
                NewsViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.text_footer, parent, false)
                FooterViewHolder(view)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NewsViewHolder) {
            holder.bind(currentList[position])
        } else if (holder is FooterViewHolder) {
            holder.bind()
        }
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.news_title)
        private val description: TextView = itemView.findViewById(R.id.news_description)
        private val newsDate: TextView = itemView.findViewById(R.id.news_date)
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

            var imageVisibility = View.GONE
            if (news.urlToImage != null) {
                setImage(news.urlToImage)
                imageVisibility = View.VISIBLE
            }
            image.visibility = imageVisibility
        }

        private fun setImage(url: String) {
            Glide
                .with(itemView)
                .load(url)
                .into(image)
        }

    }


    inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text_for_footer)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(-1)
            }
        }

        fun bind() {
            textView.text = itemView.resources.getString(R.string.end_of_list)
        }
    }
}
