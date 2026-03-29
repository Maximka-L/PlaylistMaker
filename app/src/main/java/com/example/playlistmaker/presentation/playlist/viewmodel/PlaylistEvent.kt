package com.example.playlistmaker.presentation.playlist.viewmodel

sealed interface PlaylistEvent {
    data object ShowEmptyShareMessage : PlaylistEvent
    data class SharePlaylist(val text: String) : PlaylistEvent
    data object NavigateBack : PlaylistEvent
}