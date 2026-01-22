package com.example.playlistmaker.presentation.media.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.media.view_model.PlayListFragmentViewModel

class PlayListFragment : Fragment(R.layout.fragment_play_list) {

    private val viewModel: PlayListFragmentViewModel by viewModels()

    companion object {
        fun newInstance(): PlayListFragment {
            return PlayListFragment()
        }
    }
}
