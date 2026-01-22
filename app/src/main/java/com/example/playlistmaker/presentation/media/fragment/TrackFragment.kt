package com.example.playlistmaker.presentation.media.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.media.view_model.TrackFragmentViewModel

class TrackFragment : Fragment(R.layout.fragment_track) {

    private val viewModel: TrackFragmentViewModel by viewModels()

    companion object {
        fun newInstance(): TrackFragment {
            return TrackFragment()
        }
    }
}
