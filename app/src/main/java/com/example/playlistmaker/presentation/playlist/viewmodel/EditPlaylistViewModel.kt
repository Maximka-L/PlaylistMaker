package com.example.playlistmaker.presentation.playlist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Playlist

import com.example.playlistmaker.domain.usecase.PlaylistUseCase
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistUseCase: PlaylistUseCase
) : CreatePlaylistViewModel(playlistUseCase) {

    private val _originalPlaylist = MutableLiveData<Playlist?>()
    val originalPlaylist: LiveData<Playlist?> = _originalPlaylist

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val playlist = playlistUseCase.getPlaylistById(playlistId)
            _originalPlaylist.value = playlist

            (coverUri as MutableLiveData).value = playlist.coverPath.ifBlank { null }
        }
    }

    fun getOriginalPlaylist(): Playlist? = _originalPlaylist.value

    fun updatePlaylist(name: String, description: String, coverPath: String) {
        val original = _originalPlaylist.value ?: return
        viewModelScope.launch {
            val updated = original.copy(
                name = name,
                description = description,
                coverPath = coverPath
            )
            playlistUseCase.updatePlaylist(updated)
        }
    }
}