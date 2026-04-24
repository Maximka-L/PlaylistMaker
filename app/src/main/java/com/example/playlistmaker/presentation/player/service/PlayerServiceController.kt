package com.example.playlistmaker.presentation.player.service

interface PlayerServiceController {
    fun prepare(url: String, artistName: String, trackName: String)
    fun play()
    fun pause()
    fun isPlaying(): Boolean
    fun currentPositionMs(): Int
    fun setOnPlayerStateListener(listener: PlayerStateListener)
    fun showForeground()
    fun hideForeground()
}

interface PlayerStateListener {
    fun onPlaying()
    fun onPaused()
    fun onCompleted()
}