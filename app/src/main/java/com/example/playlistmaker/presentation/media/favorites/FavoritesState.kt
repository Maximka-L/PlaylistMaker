package com.example.playlistmaker.presentation.media.favorites

import com.example.playlistmaker.domain.models.Track

sealed interface FavoritesState {
    data object Empty : FavoritesState
    data class Content(val tracks: List<Track>) : FavoritesState
}