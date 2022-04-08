package com.example.newsapp2.ui.fragment

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.lifecycleScope
import com.example.newsapp2.data.room.NewsDataBase
import com.example.newsapp2.databinding.FragmentNewsWebViewBinding
import com.example.newsapp2.tools.LogicForWebView
import kotlinx.coroutines.*

class NewsWebViewFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewsWebViewBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            binding.bindWebView()
        }
        return binding.root
    }

    private suspend fun FragmentNewsWebViewBinding.bindWebView() {
        val args = NewsWebViewFragmentArgs.fromBundle(requireArguments())
        val logic = LogicForWebView(NewsDataBase.getInstance(requireContext()))
        val url = logic.getUrl(args.articleId)
        showWebView(url, webView)
    }

    private fun showWebView(url: String, webView: WebView) {
        webView.apply {
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url: String = request?.url.toString()
                    loadUrl(url)
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
//                    Log.e(ContentValues.TAG, binding.progressBar.progress.toString())
//                    binding.progressBar.progress = progress
//                    if (progress == 100) {
//                        binding.progressBar.visibility = View.GONE
//                    }
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



}
