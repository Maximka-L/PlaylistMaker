package com.example.playlistmaker.domain.player

interface AudioPlayer {
    fun prepare(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    )

    fun play()
    fun pause()
    fun release()

    fun currentPositionMs(): Int
}
