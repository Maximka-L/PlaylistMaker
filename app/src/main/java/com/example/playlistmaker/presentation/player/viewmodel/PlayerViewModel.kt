package com.example.playlistmaker.presentation.player.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.player.AudioPlayer

class PlayerViewModel(
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private var isPrepared = false
    private var isPlaying = false

    private val _time = MutableLiveData("00:00")
    val time: LiveData<String> = _time

    private val _isPlayingLive = MutableLiveData(false)
    val isPlayingLive: LiveData<Boolean> = _isPlayingLive

    fun prepare(url: String) {
        audioPlayer.prepare(
            url = url,
            onPrepared = {
                isPrepared = true
                _isPlayingLive.postValue(false)
            },
            onCompletion = {
                reset()
            }
        )
    }

    fun toggle() {
        if (!isPrepared) return
        if (isPlaying) pause() else play()
    }

    private fun play() {
        audioPlayer.play()
        isPlaying = true
        _isPlayingLive.value = true
        startTimer()
    }

    private fun pause() {
        audioPlayer.pause()
        isPlaying = false
        _isPlayingLive.value = false
        stopTimer()
    }

    private fun reset() {
        pause()
        _time.value = "00:00"
    }

    private val updateTime = object : Runnable {
        override fun run() {
            val ms = audioPlayer.currentPositionMs()
            _time.value = formatTime(ms)
            handler.postDelayed(this, 500)
        }
    }

    private fun startTimer() = handler.post(updateTime)
    private fun stopTimer() = handler.removeCallbacks(updateTime)

    private fun formatTime(ms: Int): String {
        val sec = ms / 1000
        return String.format("%02d:%02d", sec / 60, sec % 60)
    }

    override fun onCleared() {
        stopTimer()
        audioPlayer.release()
        super.onCleared()
    }
}