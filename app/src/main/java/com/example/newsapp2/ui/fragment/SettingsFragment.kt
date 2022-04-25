package com.example.newsapp2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.newsapp2.databinding.FragmentSettingsBinding
import com.example.newsapp2.tools.TypeSetting

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val navController = NavHostFragment.findNavController(this)

        binding.themeCard.setOnClickListener {
            navController.navigate(getAction(TypeSetting.Theme))

        }

        binding.followSourceCard.setOnClickListener {
            navController.navigate(getAction(TypeSetting.Follow))
        }

        binding.blockSourceCard.setOnClickListener {
            navController.navigate(getAction(TypeSetting.BlockSource))
        }

        return binding.root
    }

    private fun getAction(typeSetting: TypeSetting) =
        MainFragmentDirections.actionMainFragmentToCurrentSettingFragment(typeSetting)
}