package com.example.playlistmaker.presentation.search.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.search.ui.SearchScreen
import com.example.playlistmaker.presentation.search.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        observeViewModel()

        return ComposeView(requireContext()).apply {

            setContent {

                SearchScreen(
                    viewModel = viewModel,

                    onTrackClick = { track ->
                        viewModel.onTrackClicked(track)
                    }
                )
            }
        }
    }

    private fun observeViewModel() {

        viewModel.openTrackEvent.observe(viewLifecycleOwner) { event ->

            event.getContentIfNotHandled()?.let { track ->

                openPlayer(track)
            }
        }
    }

    private fun openPlayer(track: Track) {

        findNavController().navigate(
            SearchFragmentDirections
                .actionSearchFragmentToAudioPlayerFragment(track)
        )
    }
}