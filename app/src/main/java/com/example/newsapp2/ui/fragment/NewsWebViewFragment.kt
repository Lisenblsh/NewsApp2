package com.example.newsapp2.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.newsapp2.R
import com.example.newsapp2.data.room.NewsDataBase
import com.example.newsapp2.data.room.TypeSource
import com.example.newsapp2.databinding.FragmentNewsWebViewBinding
import com.example.newsapp2.tools.ImageFun
import com.example.newsapp2.tools.LogicForWebView
import kotlinx.coroutines.launch


class NewsWebViewFragment : Fragment() {

    private lateinit var binding: FragmentNewsWebViewBinding
    private lateinit var logic: LogicForWebView

    private var isMenuClosed = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsWebViewBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            binding.initElement()
        }
        return binding.root
    }

    private fun FragmentNewsWebViewBinding.initElement() {
        lifecycleScope.launch {
            bindWebView()
        }
        initSwipeRefresh()
    }

    private suspend fun FragmentNewsWebViewBinding.bindWebView() {
        val args = NewsWebViewFragmentArgs.fromBundle(requireArguments())
        logic = LogicForWebView(NewsDataBase.getInstance(requireContext()), args.articleId)
        val url = logic.getUrl()
        if (url == "") {
            NavHostFragment.findNavController(this@NewsWebViewFragment).popBackStack()
            return
        }
        initWebView(url, webView)
        initMenu()
    }

    private fun FragmentNewsWebViewBinding.initWebView(url: String, webView: WebView) {
        webView.apply {
            setOnScrollChangeListener { _, _, _, _, _ ->
                likeButton.visibility = View.GONE
                followButton.visibility = View.GONE
                blockButton.visibility = View.GONE
                shareButton.visibility = View.GONE
                isMenuClosed = true
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    loadUrl(request?.url.toString())
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
            if (isMenuClosed) {
                likeButton.visibility = View.VISIBLE
                followButton.visibility = View.VISIBLE
                blockButton.visibility = View.VISIBLE
                shareButton.visibility = View.VISIBLE
                isMenuClosed = false
            } else {
                likeButton.visibility = View.GONE
                followButton.visibility = View.GONE
                blockButton.visibility = View.GONE
                shareButton.visibility = View.GONE
                isMenuClosed = true
            }
        }

        var isLikedNews = logic.isLikedNews()
        var isFollowedSource = logic.isFollowedSource()
        var isBlockedSource = logic.isBlockedSource()

        val imageFun = ImageFun()

        if (isLikedNews)
            imageFun.setImage(R.drawable.ic_baseline_favorite_24, likeButton)
        if (isFollowedSource) {
            imageFun.setImage(R.drawable.ic_baseline_add_circle_24, followButton)
            blockButton.alpha = .5f
        }
        if (isBlockedSource) {
            imageFun.setImage(R.drawable.ic_baseline_remove_circle_24, blockButton)
            followButton.alpha = .5f
        }
        //Установка значений если кнопки были нажаты ранее

        likeButton.setOnClickListener {
            isLikedNews = if (isLikedNews) {
                lifecycleScope.launch { logic.unlikeNews() }
                imageFun.setImage(R.drawable.ic_baseline_favorite_border_24, it)
                showMessage(resources.getString(R.string.unlikedArticle))
                false
            } else {
                lifecycleScope.launch { logic.likeNews() }
                imageFun.setImage(R.drawable.ic_baseline_favorite_24, it)
                showMessage(resources.getString(R.string.likedArticle))
                true
            }
        }//Новость понравилась

        followButton.setOnClickListener {
            if (!isBlockedSource) {
                isFollowedSource = if (isFollowedSource) {
                    lifecycleScope.launch { logic.removeSource(TypeSource.FollowSource) }
                    imageFun.setImage(R.drawable.ic_baseline_add_circle_outline_24, it)
                    showMessage(resources.getString(R.string.unfollow_source, logic.getDomain()))
                    blockButton.alpha = 1f
                    false
                } else {
                    lifecycleScope.launch { logic.saveSource(TypeSource.FollowSource) }
                    imageFun.setImage(R.drawable.ic_baseline_add_circle_24, it)
                    showMessage(resources.getString(R.string.follow_source, logic.getDomain()))
                    blockButton.alpha = .5f
                    true
                }
            }
        }//Подписка на источник

        blockButton.setOnClickListener {
            if (!isFollowedSource) {
                isBlockedSource = if (isBlockedSource) {
                    lifecycleScope.launch { logic.removeSource(TypeSource.BlockSource) }
                    imageFun.setImage(R.drawable.ic_baseline_remove_circle_outline_24, it)
                    showMessage(resources.getString(R.string.unblock_source, logic.getDomain()))
                    followButton.alpha = 1f
                    false
                } else {
                    lifecycleScope.launch { logic.saveSource(TypeSource.BlockSource) }
                    imageFun.setImage(R.drawable.ic_baseline_remove_circle_24, it)
                    showMessage(resources.getString(R.string.block_source, logic.getDomain()))
                    followButton.alpha = .5f
                    true
                }
            }
        }//Блокировка новостей от источника

        shareButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, logic.getTextForMessage())
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, resources.getString(R.string.send)))
        }//Поделиться новостью
    }

    private var toast: Toast? = null
    private fun showMessage(message: String) {
        if(toast != null){
            toast?.cancel()
        }
        toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).also { it.show() }
    }
}
