package com.example.playlistmaker.presentation.media.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.presentation.media.fragment.PlayListFragment
import com.example.playlistmaker.presentation.media.fragment.TrackFragment

class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> TrackFragment.newInstance()
            else -> PlayListFragment.newInstance()
        }
}


