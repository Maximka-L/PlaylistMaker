package com.example.playlistmaker.presentation.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.usecase.PlaylistUseCase
import kotlinx.coroutines.launch

class PlayListFragmentViewModel(
    private val playlistUseCase: PlaylistUseCase
) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>(emptyList())
    val playlists: LiveData<List<Playlist>> = _playlists

    init {
        viewModelScope.launch {
            playlistUseCase.getPlaylists().collect { playlists ->
                _playlists.postValue(playlists)
            }
        }
    }
}