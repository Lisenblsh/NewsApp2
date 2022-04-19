package com.example.newsapp2.ui.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.newsapp2.R
import com.example.newsapp2.data.room.NewsDataBase
import com.example.newsapp2.data.room.TypeSource
import com.example.newsapp2.databinding.FragmentNewsWebViewBinding
import com.example.newsapp2.tools.LogicForWebView
import kotlinx.coroutines.launch


class NewsWebViewFragment : Fragment() {

    private lateinit var binding: FragmentNewsWebViewBinding

    private lateinit var logic: LogicForWebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsWebViewBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            binding.initElement()
        }
        return binding.root
    }

    private fun FragmentNewsWebViewBinding.initElement() {
        lifecycleScope.launch {
            bindWebView()
            initMenu()
        }
        initSwipeRefresh()
    }

    private suspend fun FragmentNewsWebViewBinding.bindWebView() {
        val args = NewsWebViewFragmentArgs.fromBundle(requireArguments())
        logic = LogicForWebView(NewsDataBase.getInstance(requireContext()), args.articleId)
        val url = logic.getUrl()
        initWebView(url, webView)
    }

    private fun FragmentNewsWebViewBinding.initWebView(url: String, webView: WebView) {
        webView.apply {
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    loadUrl(request?.url.toString())
                    return true
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                    loadUrl(url)
                    return true
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = progress
                    if (progress == 100) {
                        progressBar.visibility = View.GONE
                    }
                }
            }
            setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_BACK -> if (canGoBack()) {
                                goBack()
                                return true
                            }
                        }
                    }
                    return false
                }
            })//Отработка действия "назад" для webView
            settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                loadsImagesAutomatically = true
            }
            loadUrl(url)
        }
    }

    private fun FragmentNewsWebViewBinding.initSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = true
            webView.reload()
            swipeRefresh.isRefreshing = false
        }
    }

    private suspend fun FragmentNewsWebViewBinding.initMenu() {

        menuButton.setOnClickListener {

        }

        var isLikedNews = logic.isLikedNews()
        var isFollowedSource = logic.isFollowedSource()
        var isBlockedSource = logic.isBlockedSource()

        if (isLikedNews) setImage(R.drawable.ic_baseline_favorite_24, likeButton)
        if (isFollowedSource) setImage(R.drawable.ic_baseline_add_circle_24, followButton)
        if (isFollowedSource) setImage(R.drawable.ic_baseline_remove_circle_24, blockButton)

        likeButton.setOnClickListener {
            isLikedNews = if (isLikedNews) {
                lifecycleScope.launch { logic.unlikeNews() }
                setImage(R.drawable.ic_baseline_favorite_border_24, it)
                showMessage(resources.getString(R.string.unlikedArticle))
                false
            } else {
                lifecycleScope.launch { logic.likeNews() }
                setImage(R.drawable.ic_baseline_favorite_24, it)
                showMessage(resources.getString(R.string.likedArticle))
                true
            }
        }

        followButton.setOnClickListener {
            if(!isBlockedSource){
                isFollowedSource = if (isFollowedSource) {
                    lifecycleScope.launch { logic.removeSource(TypeSource.FollowSource) }
                    setImage(R.drawable.ic_baseline_add_circle_outline_24, it)
                    showMessage(resources.getString(R.string.unfollowSource, logic.getDomain()))
                    blockButton.alpha = 1f
                    false
                } else {
                    lifecycleScope.launch { logic.saveSource(TypeSource.FollowSource) }
                    setImage(R.drawable.ic_baseline_add_circle_24, it)
                    showMessage(resources.getString(R.string.followSource, logic.getDomain()))
                    blockButton.alpha = .5f
                    true
                }
            }
        }

        blockButton.setOnClickListener {
            if (!isFollowedSource) {
                isBlockedSource = if (isBlockedSource) {
                    lifecycleScope.launch { logic.removeSource(TypeSource.BlockSource) }
                    setImage(R.drawable.ic_baseline_remove_circle_outline_24, it)
                    showMessage(resources.getString(R.string.unblockSource, logic.getDomain()))
                    followButton.alpha = 1f
                    false
                } else {
                    lifecycleScope.launch { logic.saveSource(TypeSource.BlockSource) }
                    setImage(R.drawable.ic_baseline_remove_circle_24, it)
                    showMessage(resources.getString(R.string.blockSource, logic.getDomain()))
                    followButton.alpha = .5f
                    true
                }
            }
        }
    }
    
    private fun setImage(image: Int, view: View){
        if(view is ImageView) {
            Glide
                .with(this@NewsWebViewFragment)
                .load(image)
                .into(view)
        }
    }

    private fun showMessage(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
