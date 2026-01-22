package com.example.playlistmaker.presentation.media.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.presentation.media.adapter.PagerAdapter
import com.example.playlistmaker.presentation.media.view_model.MediaViewModel
import com.google.android.material.tabs.TabLayoutMediator

class MediaFragment : Fragment(R.layout.fragment_media) {

    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MediaViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMediaBinding.bind(view)

        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = PagerAdapter(this)
        binding.pager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tracks)
                1 -> getString(R.string.playlists)
                else -> ""
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
