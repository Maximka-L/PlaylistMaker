package com.example.playlistmaker.presentation.player.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.player.AudioPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val PROGRESS_UPDATE_DELAY = 300L

class PlayerViewModel(
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private var isPrepared = false
    private var isPlaying = false

    private var progressJob: Job? = null

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
                _time.postValue("00:00")
            },
            onCompletion = {

                stopProgressUpdates()
                isPlaying = false
                _isPlayingLive.postValue(false)
                _time.postValue("00:00")
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
        startProgressUpdates()
    }

    private fun pause() {
        audioPlayer.pause()
        isPlaying = false
        _isPlayingLive.value = false
        stopProgressUpdates()
    }

    private fun startProgressUpdates() {
        if (progressJob?.isActive == true) return

        progressJob = viewModelScope.launch {
            while (isActive && isPlaying) {
                val ms = audioPlayer.currentPositionMs()
                _time.value = formatTime(ms)
                delay(PROGRESS_UPDATE_DELAY)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun formatTime(ms: Int): String {
        val sec = ms / 1000
        return String.format("%02d:%02d", sec / 60, sec % 60)
    }

    override fun onCleared() {
        stopProgressUpdates()
        audioPlayer.release()
        super.onCleared()
    }
}
