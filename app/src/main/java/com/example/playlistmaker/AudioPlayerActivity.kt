package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.models.Track

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var currentTrack: Track? = null


    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            val currentPosition = mediaPlayer?.currentPosition ?: 0
            binding.currentTimeTextView.text = formatTime(currentPosition)
            handler.postDelayed(this, UPDATE_DELAY_MS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTrack = intent.getParcelableExtra("track")

        if (currentTrack != null) {
            showTrackInfo(currentTrack!!)
        } else {
            finish()
            return
        }

        binding.toolbar.setOnClickListener { finish() }

        binding.playButton.setOnClickListener { togglePlayback() }
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

    private fun togglePlayback() {
        val track = currentTrack ?: return
        val url = track.previewUrl ?: return

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener {
                    start()
                    binding.playButton.setImageResource(R.drawable.ic_pause)
                    startUpdatingTime()
                }
                setOnCompletionListener {
                    stopUpdatingTime()
                    binding.playButton.setImageResource(R.drawable.ic_play)
                    binding.currentTimeTextView.text = getString(R.string.time_zero)

                }
            }
        } else {
            if (isPlaying) {
                pausePlayback()
            } else {
                resumePlayback()
            }
        }
    }

    private fun pausePlayback() {
        mediaPlayer?.pause()
        isPlaying = false
        stopUpdatingTime()
        binding.playButton.setImageResource(R.drawable.ic_play)
    }

    private fun resumePlayback() {
        mediaPlayer?.start()
        isPlaying = true
        startUpdatingTime()
        binding.playButton.setImageResource(R.drawable.ic_pause)
    }

    private fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun startUpdatingTime() {
        handler.post(updateTimeRunnable)
    }

    private fun stopUpdatingTime() {
        handler.removeCallbacks(updateTimeRunnable)
    }

    private companion object {
        private const val UPDATE_DELAY_MS = 500L

    }


    override fun onPause() {
        super.onPause()
        if (isPlaying) pausePlayback()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopUpdatingTime()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
