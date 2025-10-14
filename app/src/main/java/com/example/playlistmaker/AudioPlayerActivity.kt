package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
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
            return
        }

        // Кнопка "Назад"
        binding.toolbar.setOnClickListener {
            finish()
        }

        // Кнопка "Play"
        binding.playButton.setOnClickListener {
            startPlayback()
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

            // Glide с placeholder и плавной анимацией
            Glide.with(this@AudioPlayerActivity)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.ic_placeholder1) // показывается во время загрузки
                .error(R.drawable.ic_placeholder1)       // если ошибка загрузки
                .fallback(R.drawable.ic_placeholder1)    // если ссылка вообще null
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .centerCrop()
                .into(coverImageView)
        }
    }

    private fun startPlayback() {
        val track = currentTrack ?: return


    }
}
