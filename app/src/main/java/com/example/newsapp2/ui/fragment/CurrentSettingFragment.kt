package com.example.newsapp2.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.newsapp2.R
import com.example.newsapp2.data.room.TypeSource
import com.example.newsapp2.databinding.FragmentCurrentSettingBinding
import com.example.newsapp2.tools.TypeSetting
import com.example.newsapp2.ui.adapters.viewHolders.SelectViewHolder
import com.example.newsapp2.ui.adapters.viewHolders.SourceViewHolder
import kotlinx.coroutines.launch


class CurrentSettingFragment : Fragment() {

    private lateinit var binding: FragmentCurrentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrentSettingBinding.inflate(inflater, container, false)
        val args = CurrentSettingFragmentArgs.fromBundle(requireArguments())
        lifecycleScope.launch {
            binding.showSetting(args.typeSetting)
        }

        return binding.root
    }

    private suspend fun FragmentCurrentSettingBinding.showSetting(typeSetting: TypeSetting) {
        when (typeSetting) {
            TypeSetting.Theme -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.select_setting_layout, root, false)
                object : SelectViewHolder(view, typeSetting) {
                    override fun getPreferences(): SharedPreferences {
                        return requireActivity().getSharedPreferences(
                            "appSettings",
                            Context.MODE_PRIVATE
                        )
                    }

                    override fun forRestartActivity() {
                        val intent = requireActivity().intent
                        requireActivity().finish()
                        startActivity(intent)
                    }

                }.bind()
                root.addView(view)
            }
            TypeSetting.ApiSource -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.select_setting_layout, root, false)
                object : SelectViewHolder(view, typeSetting) {
                    override fun getPreferences(): SharedPreferences {
                        return requireActivity().getSharedPreferences(
                            "appSettings",
                            Context.MODE_PRIVATE
                        )
                    }

                    override fun forRestartActivity() {
                        val intent = requireActivity().intent
                        requireActivity().finish()
                        startActivity(intent)
                    }

                }.bind()
                root.addView(view)
            }
            TypeSetting.Follow -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.source_setting_layout, root, false)
                SourceViewHolder(view, TypeSource.FollowSource).bind()
                root.addView(view)
            }
            TypeSetting.BlockSource -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.source_setting_layout, root, false)
                SourceViewHolder(view, TypeSource.BlockSource).bind()
                root.addView(view)
            }
        }
    }

}
