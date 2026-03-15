package com.example.playlistmaker.domain.usecase.impl

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.PlaylistRepository
import com.example.playlistmaker.domain.usecase.PlaylistUseCase
import kotlinx.coroutines.flow.Flow

class PlaylistUseCaseImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistUseCase {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistRepository.createPlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {
        return playlistRepository.addTrackToPlaylist(track, playlist)
    }
}