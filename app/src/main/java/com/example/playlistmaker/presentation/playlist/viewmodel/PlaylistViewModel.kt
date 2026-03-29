package com.example.playlistmaker.presentation.playlist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.usecase.PlaylistUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistUseCase: PlaylistUseCase
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    private val _tracks = MutableLiveData<List<Track>>(emptyList())
    val tracks: LiveData<List<Track>> = _tracks

    private val _duration = MutableLiveData("0")
    val duration: LiveData<String> = _duration

    private val _events = MutableSharedFlow<PlaylistEvent>()
    val events = _events.asSharedFlow()

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val playlist = playlistUseCase.getPlaylistById(playlistId)
            _playlist.value = playlist

            val tracks = playlistUseCase.getTracksByIds(playlist.trackIds)
            _tracks.value = tracks

            calculateDuration(tracks)
        }
    }

    fun removeTrack(track: Track) {
        val currentPlaylist = _playlist.value ?: return

        viewModelScope.launch {
            playlistUseCase.removeTrackFromPlaylist(track, currentPlaylist)

            val updatedPlaylist = playlistUseCase.getPlaylistById(currentPlaylist.id)
            _playlist.value = updatedPlaylist

            val updatedTracks = playlistUseCase.getTracksByIds(updatedPlaylist.trackIds)
            _tracks.value = updatedTracks

            calculateDuration(updatedTracks)
        }
    }

    fun deletePlaylist() {
        val currentPlaylist = _playlist.value ?: return

        viewModelScope.launch {
            playlistUseCase.deletePlaylist(currentPlaylist)
            _events.emit(PlaylistEvent.NavigateBack)
        }
    }

    fun onShareClicked(tracksCountText: String) {
        val currentPlaylist = _playlist.value ?: return
        val currentTracks = _tracks.value ?: emptyList()

        viewModelScope.launch {
            if (currentTracks.isEmpty()) {
                _events.emit(PlaylistEvent.ShowEmptyShareMessage)
            } else {
                val shareText = buildPlaylistShareText(currentPlaylist, currentTracks, tracksCountText)
                _events.emit(PlaylistEvent.SharePlaylist(shareText))
            }
        }
    }

    private fun buildPlaylistShareText(playlist: Playlist, tracks: List<Track>, tracksCountText: String): String {
        val descriptionPart = if (playlist.description.isBlank()) "" else "${playlist.description}\n"

        val tracksText = tracks.mapIndexed { index, track ->
            "${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})"
        }.joinToString("\n")

        return buildString {
            appendLine(playlist.name)
            if (descriptionPart.isNotEmpty()) append(descriptionPart)
            appendLine(tracksCountText)
            append(tracksText)
        }.trim()
    }

    private fun calculateDuration(tracks: List<Track>) {
        val durationSumMillis = tracks.sumOf { parseTrackTimeToMillis(it.trackTime) }
        val totalMinutes = durationSumMillis / 60000
        _duration.value = totalMinutes.toString()
    }

    private fun parseTrackTimeToMillis(trackTime: String): Long {
        val parts = trackTime.split(":")
        if (parts.size != 2) return 0L

        val minutes = parts[0].toLongOrNull() ?: 0L
        val seconds = parts[1].toLongOrNull() ?: 0L

        return (minutes * 60 + seconds) * 1000
    }
}