package com.example.newsapp2.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.newsapp2.R
import com.example.newsapp2.databinding.FragmentMainBinding
import com.example.newsapp2.ui.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MainFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.bindViewPager()
        return binding.root
    }
    private fun FragmentMainBinding.bindViewPager() {
        val fragmentList = arrayListOf(
            NewsFragment(),
            HomeFragment(),
            SettingsFragment()
        )

        val adapter = ViewPagerAdapter(fragmentList, childFragmentManager, lifecycle)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tabCustom = LayoutInflater.from(requireContext())
                .inflate(R.layout.tab_item, null) as LinearLayout
            val tabImg = tabCustom.findViewById<ImageView>(R.id.tabImg)
            val tabText = tabCustom.findViewById<TextView>(R.id.tabText)
            when (position) {
                0 -> {
                    tabText.text = "Новости"
                    setIcon(R.drawable.newspaper, tabImg)

                }
                1 -> {
                    tabText.text = "Домашняя"
                    setIcon(R.drawable.home, tabImg)

                }
                2 -> {
                    tabText.text = "Настройки"
                    setIcon(R.drawable.settings, tabImg)
                }
            }
            tab.customView = tabCustom
        }.attach()
    }

    private fun setIcon(icon: Int, imageView: ImageView){
        Glide
            .with(this)
            .load(icon)
            .into(imageView)
    }
}
