package com.example.playlistmaker.presentation.media

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(hostFragment: FragmentActivity) : FragmentStateAdapter(hostFragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) TrackFragment() else PlayListFragment()
    }
}