package com.example.playlistmaker.presentation.player.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.player.AudioPlayer
import com.example.playlistmaker.domain.usecase.FavoritesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val PROGRESS_UPDATE_DELAY = 300L

private data class PlayerState(
    val isPrepared: Boolean = false,
    val isPlaying: Boolean = false
)

class PlayerViewModel(
    private val audioPlayer: AudioPlayer,
    private val favoritesUseCase: FavoritesUseCase
) : ViewModel() {

    private var playerState = PlayerState()

    private var progressJob: Job? = null

    private val _time = MutableLiveData("00:00")
    val time: LiveData<String> = _time

    private val _isPlayingLive = MutableLiveData(false)
    val isPlayingLive: LiveData<Boolean> = _isPlayingLive

    private var currentTrack: Track? = null

    private val _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun prepare(url: String) {
        audioPlayer.prepare(
            url = url,
            onPrepared = {
                playerState = playerState.copy(isPrepared = true, isPlaying = false)
                _isPlayingLive.postValue(false)
                _time.postValue("00:00")
            },
            onCompletion = {
                stopProgressUpdates()
                playerState = playerState.copy(isPlaying = false)
                _isPlayingLive.postValue(false)
                _time.postValue("00:00")
            }
        )
    }

    fun toggle() {
        if (!playerState.isPrepared) return
        if (playerState.isPlaying) pause() else play()
    }

    private fun play() {
        audioPlayer.play()
        playerState = playerState.copy(isPlaying = true)
        _isPlayingLive.value = true
        startProgressUpdates()
    }

    private fun pause() {
        audioPlayer.pause()
        playerState = playerState.copy(isPlaying = false)
        _isPlayingLive.value = false
        stopProgressUpdates()
    }

    fun setTrack(track: Track) {
        currentTrack = track


        viewModelScope.launch {
            _isFavorite.postValue(favoritesUseCase.isFavorite(track.trackId))
        }
    }

    fun onFavoriteClicked() {
        val track = currentTrack ?: return

        viewModelScope.launch {
            runCatching {
                favoritesUseCase.toggleFavorite(track)
            }.onSuccess { newValue ->
                _isFavorite.postValue(newValue)
            }.onFailure {

            }
        }
    }

    private fun startProgressUpdates() {
        if (progressJob?.isActive == true) return

        progressJob = viewModelScope.launch {
            while (isActive && playerState.isPlaying) {
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