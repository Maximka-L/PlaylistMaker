package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var currentTrack: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем трек из Intent
        currentTrack = intent.getParcelableExtra("track")
        if (currentTrack != null) {
            showTrackInfo(currentTrack!!)
        } else {
            finish()
        }

        // Кнопка "назад"
        binding.toolbar.setOnClickListener {
            finish()
        }

        // Плей/пауза
        binding.playButton.setOnClickListener {
            togglePlayback()
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
                .placeholder(R.drawable.placeholder_cover)
                .into(coverImageView)
        }
    }

    private fun togglePlayback() {
        val track = currentTrack ?: return

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                // TODO: добавьте воспроизведение previewUrl
                // setDataSource(track.previewUrl)
                // prepareAsync()
            }
        }

        if (isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
            binding.playButton.setImageResource(R.drawable.ic_play)
        } else {
            mediaPlayer?.start()
            isPlaying = true
            //binding.playButton.setImageResource(R.drawable.ic_pause)
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
        isPlaying = false
        binding.playButton.setImageResource(R.drawable.ic_play)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}