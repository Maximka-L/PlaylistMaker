package com.example.playlistmaker.presentation.media.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.usecase.FavoritesUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesUseCase: FavoritesUseCase
) : ViewModel() {

    private val _state = MutableLiveData<FavoritesState>(FavoritesState.Empty)
    val state: LiveData<FavoritesState> = _state

    init {
        viewModelScope.launch {
            favoritesUseCase.getFavoriteTracks().collectLatest { tracks ->
                _state.postValue(
                    if (tracks.isEmpty()) FavoritesState.Empty
                    else FavoritesState.Content(tracks)
                )
            }
        }
    }
}