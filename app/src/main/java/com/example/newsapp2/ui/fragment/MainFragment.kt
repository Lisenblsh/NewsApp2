package com.example.newsapp2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.newsapp2.R
import com.example.newsapp2.databinding.FragmentMainBinding
import com.example.newsapp2.ui.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            val tabImg = tabCustom.findViewById<ImageView>(R.id.tab_img)
            val tabText = tabCustom.findViewById<TextView>(R.id.tab_text)
            when (position) {
                0 -> {
                    tabText.text = resources.getString(R.string.news)
                    setIcon(R.drawable.newspaper, tabImg)

                }
                1 -> {
                    tabText.text = resources.getString(R.string.home)
                    setIcon(R.drawable.home, tabImg)

                }
                2 -> {
                    tabText.text = resources.getString(R.string.settings)
                    setIcon(R.drawable.settings, tabImg)
                }
            }
            tab.customView = tabCustom
        }.attach()
    }

    private fun setIcon(icon: Int, imageView: ImageView) {
        Picasso.get()
            .load(icon)
            .into(imageView)
    }
}
