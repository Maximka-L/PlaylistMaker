package com.example.playlistmaker.presentation.search
import com.example.playlistmaker.domain.models.Track

sealed class SearchScreenState {


    data class History(val tracks: List<Track>) : SearchScreenState()


    data class Content(val tracks: List<Track>) : SearchScreenState()


    data class Empty(val isInternetError: Boolean) : SearchScreenState()


    object Loading : SearchScreenState()
}