package com.example.playlistmaker.presentation.player

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayerViewModel : ViewModel() {

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    private var isPrepared = false
    private var isPlaying = false

    private val _time = MutableLiveData("00:00")
    val time: LiveData<String> = _time

    private val _isPlayingLive = MutableLiveData(false)
    val isPlayingLive: LiveData<Boolean> = _isPlayingLive

    fun prepare(url: String) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                isPrepared = true
                _isPlayingLive.value = false
            }
            setOnCompletionListener {
                reset()
            }
        }
    }

    fun toggle() {
        if (!isPrepared) return

        if (isPlaying) pause() else play()
    }

    private fun play() {
        mediaPlayer?.start()
        isPlaying = true
        _isPlayingLive.value = true
        startTimer()
    }

    private fun pause() {
        mediaPlayer?.pause()
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
            val ms = mediaPlayer?.currentPosition ?: 0
            _time.value = formatTime(ms)
            handler.postDelayed(this, 500)
        }
    }

    private fun startTimer() {
        handler.post(updateTime)
    }

    private fun stopTimer() {
        handler.removeCallbacks(updateTime)
    }

    private fun formatTime(ms: Int): String {
        val sec = ms / 1000
        return String.format("%02d:%02d", sec / 60, sec % 60)
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
