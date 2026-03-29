package com.example.playlistmaker.presentation.media.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentTrackBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.media.favorites.FavoritesState
import com.example.playlistmaker.presentation.media.favorites.FavoritesViewModel
import com.example.playlistmaker.presentation.search.adapter.TracksAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrackFragment : Fragment(R.layout.fragment_track) {

    private var _binding: FragmentTrackBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModel()

    private val adapter by lazy {
        TracksAdapter(
            dataset = emptyList(),
            onItemClick = { track -> openPlayer(track) }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTrackBinding.bind(view)

        binding.favoritesRecycler.adapter = adapter

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoritesState.Empty -> showEmpty()
                is FavoritesState.Content -> showContent(state.tracks)
            }
        }
    }

    private fun showEmpty() {
        binding.emptyIcon.visibility = View.VISIBLE
        binding.emptyText.visibility = View.VISIBLE
        binding.favoritesRecycler.visibility = View.GONE
    }

    private fun showContent(tracks: List<Track>) {
        binding.emptyIcon.visibility = View.GONE
        binding.emptyText.visibility = View.GONE
        binding.favoritesRecycler.visibility = View.VISIBLE
        adapter.updateDataset(tracks)
    }

    private fun openPlayer(track: Track) {
        val bundle = Bundle().apply { putParcelable("track", track) }

        requireParentFragment().findNavController().navigate(
            R.id.audioPlayerFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): TrackFragment = TrackFragment()
    }
}