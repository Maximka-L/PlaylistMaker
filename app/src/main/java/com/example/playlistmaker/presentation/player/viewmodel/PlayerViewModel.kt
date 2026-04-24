package com.example.playlistmaker.presentation.player.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.usecase.FavoritesUseCase
import com.example.playlistmaker.domain.usecase.PlaylistUseCase
import com.example.playlistmaker.presentation.player.service.PlayerServiceController
import com.example.playlistmaker.presentation.player.service.PlayerStateListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val PROGRESS_UPDATE_DELAY = 300L

sealed class PlaylistAddStatus {
    data class Added(val playlistName: String) : PlaylistAddStatus()
    data class AlreadyExists(val playlistName: String) : PlaylistAddStatus()
}

class PlayerViewModel(
    private val favoritesUseCase: FavoritesUseCase,
    private val playlistUseCase: PlaylistUseCase
) : ViewModel() {

    private var serviceController: PlayerServiceController? = null
    private var progressJob: Job? = null
    private var isPrepared = false
    private var isPlaying = false
    private var currentTrack: Track? = null

    private val _time = MutableLiveData("00:00")
    val time: LiveData<String> = _time

    private val _isPlayingLive = MutableLiveData(false)
    val isPlayingLive: LiveData<Boolean> = _isPlayingLive

    private val _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _playlists = MutableLiveData<List<Playlist>>(emptyList())
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _playlistAddStatus = MutableLiveData<PlaylistAddStatus?>(null)
    val playlistAddStatus: LiveData<PlaylistAddStatus?> = _playlistAddStatus

    init {
        observePlaylists()
    }

    private fun observePlaylists() {
        viewModelScope.launch {
            playlistUseCase.getPlaylists().collect { list ->
                _playlists.postValue(list)
            }
        }
    }

    fun onServiceConnected(controller: PlayerServiceController) {
        serviceController = controller
        controller.setOnPlayerStateListener(object : PlayerStateListener {
            override fun onPlaying() {
                isPrepared = true
                isPlaying = true
                _isPlayingLive.postValue(true)
                startProgressUpdates()
            }

            override fun onPaused() {
                isPlaying = false
                isPrepared = true
                _isPlayingLive.postValue(false)
                stopProgressUpdates()
            }

            override fun onCompleted() {
                isPlaying = false
                isPrepared = false
                _isPlayingLive.postValue(false)
                _time.postValue("00:00")
                stopProgressUpdates()
                currentTrack?.previewUrl?.let { url ->
                    prepare(
                        url = url,
                        artistName = currentTrack?.artistName ?: "",
                        trackName = currentTrack?.trackName ?: ""
                    )
                }
            }
        })
    }

    fun prepare(url: String, artistName: String, trackName: String) {
        isPrepared = false
        serviceController?.prepare(url, artistName, trackName)
    }

    fun toggle() {
        if (!isPrepared) return
        if (isPlaying) {
            serviceController?.pause()
        } else {
            serviceController?.play()
        }
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
            }
        }
    }

    fun onPlaylistClicked(playlist: Playlist) {
        val track = currentTrack ?: return
        viewModelScope.launch {
            val added = playlistUseCase.addTrackToPlaylist(track, playlist)
            _playlistAddStatus.postValue(
                if (added) PlaylistAddStatus.Added(playlist.name)
                else PlaylistAddStatus.AlreadyExists(playlist.name)
            )
        }
    }

    fun clearPlaylistAddStatus() {
        _playlistAddStatus.value = null
    }

    fun onAppInForeground() {
        serviceController?.hideForeground()
    }

    fun onAppInBackground() {
        if (isPlaying) {
            serviceController?.showForeground()
        }
    }

    private fun startProgressUpdates() {
        if (progressJob?.isActive == true) return
        progressJob = viewModelScope.launch {
            while (isActive && isPlaying) {
                val ms = serviceController?.currentPositionMs() ?: 0
                _time.value = formatTime(ms)
                delay(PROGRESS_UPDATE_DELAY)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    fun stopPlayback() {
        if (isPlaying) {
            serviceController?.pause()
        }
    }

    private fun formatTime(ms: Int): String {
        val sec = ms / 1000
        return String.format("%02d:%02d", sec / 60, sec % 60)
    }

    override fun onCleared() {
        stopProgressUpdates()
        super.onCleared()
    }
}