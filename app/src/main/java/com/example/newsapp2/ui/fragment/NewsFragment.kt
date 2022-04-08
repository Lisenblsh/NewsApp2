package com.example.newsapp2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.newsapp2.R
import com.example.newsapp2.databinding.FragmentNewsBinding
import com.example.newsapp2.ui.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class NewsFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewsBinding.inflate(inflater, container, false)
        binding.bindViewPager()
        return binding.root
    }

    private fun FragmentNewsBinding.bindViewPager(){
        val fragmentList = arrayListOf(
            RegularNewsFragment(),
            FavoriteNewsFragment()
        )
        val adapter =
            ViewPagerAdapter(fragmentList, childFragmentManager, lifecycle)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false


        TabLayoutMediator(tabLayout, viewPager) {tab, position ->
            when(position) {
                0 -> tab.text = resources.getString(R.string.all_news)
                1 -> tab.text = resources.getString(R.string.favorite)
            }
        }.attach()
    }
}