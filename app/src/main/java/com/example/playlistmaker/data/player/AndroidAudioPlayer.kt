package com.example.playlistmaker.data.player

import android.media.MediaPlayer
import com.example.playlistmaker.domain.player.AudioPlayer

class AndroidAudioPlayer(
    private val mediaPlayer: MediaPlayer
) : AudioPlayer {

    override fun prepare(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.setOnPreparedListener { onPrepared() }
        mediaPlayer.setOnCompletionListener { onCompletion() }
        mediaPlayer.prepareAsync()
    }

    override fun play() = mediaPlayer.start()
    override fun pause() = mediaPlayer.pause()
    override fun release() = mediaPlayer.release()

    override fun currentPositionMs(): Int = mediaPlayer.currentPosition
}
