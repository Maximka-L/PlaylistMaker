package com.example.playlistmaker.presentation.playlist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.usecase.PlaylistUseCase
import kotlinx.coroutines.launch

open class CreatePlaylistViewModel(
    private val playlistUseCase: PlaylistUseCase
) : ViewModel() {

    private val _coverUri = MutableLiveData<String?>(null)
    val coverUri: LiveData<String?> = _coverUri

    fun setCoverUri(uri: String) {
        _coverUri.value = uri
    }

    fun createPlaylist(
        name: String,
        description: String,
        coverPath: String = ""
    ) {
        viewModelScope.launch {
            val playlist = Playlist(
                name = name,
                description = description,
                coverPath = coverPath,
                trackIds = emptyList(),
                tracksCount = 0
            )
            playlistUseCase.createPlaylist(playlist)
        }
    }
}