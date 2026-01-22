package com.example.playlistmaker.presentation.player.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class AudioPlayerFragment : Fragment(R.layout.fragment_audio_player) {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerViewModel by viewModel()
    private var currentTrack: Track? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAudioPlayerBinding.bind(view)

        // Получаем аргумент из Navigation
        currentTrack = arguments?.getParcelable("track")

        if (currentTrack == null) {
            findNavController().popBackStack()
            return
        }

        showTrackInfo(currentTrack!!)

        currentTrack?.previewUrl?.let {
            viewModel.prepare(it)
        }

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.time.observe(viewLifecycleOwner) {
            binding.currentTimeTextView.text = it
        }

        viewModel.isPlayingLive.observe(viewLifecycleOwner) { playing ->
            binding.playButton.setImageResource(
                if (playing) R.drawable.ic_pause else R.drawable.ic_play
            )
        }
    }

    private fun setupListeners() {
        binding.playButton.setOnClickListener {
            viewModel.toggle()
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showTrackInfo(track: Track) {
        with(binding) {
            trackNameTextView.text = track.trackName
            artistNameTextView.text = track.artistName
            trackDurationTextView.text = track.getFormattedTime()
            albumTextView.text = track.collectionName ?: getString(R.string.unknown_album)
            yearTextView.text = track.getReleaseYear()
            genreTextView.text = track.primaryGenreName ?: getString(R.string.unknown_genre)
            countryTextView.text = track.country ?: getString(R.string.unknown_country)

            Glide.with(this@AudioPlayerFragment)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.ic_placeholder1)
                .error(R.drawable.ic_placeholder1)
                .fallback(R.drawable.ic_placeholder1)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .centerCrop()
                .into(coverImageView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}