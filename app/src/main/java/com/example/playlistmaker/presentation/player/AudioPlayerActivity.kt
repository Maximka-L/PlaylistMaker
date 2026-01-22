package com.example.playlistmaker.presentation.player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAudioPlayerBinding
    private val viewModel: PlayerViewModel by viewModel()
    private var currentTrack: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            v.updatePadding(
                top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            )
            insets
        }

        currentTrack = intent.getParcelableExtra("track")
        if (currentTrack == null) {
            finish()
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
        viewModel.time.observe(this) {
            binding.currentTimeTextView.text = it
        }

        viewModel.isPlayingLive.observe(this) { playing ->
            binding.playButton.setImageResource(
                if (playing) R.drawable.ic_pause else R.drawable.ic_play
            )
        }
    }

    private fun setupListeners() {
        binding.playButton.setOnClickListener {
            viewModel.toggle()
        }

        binding.toolbar.setOnClickListener {
            finish()
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

            Glide.with(this@AudioPlayerActivity)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.ic_placeholder1)
                .error(R.drawable.ic_placeholder1)
                .fallback(R.drawable.ic_placeholder1)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .centerCrop()
                .into(coverImageView)
        }
    }
}
