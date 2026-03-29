package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistUseCase {
    suspend fun createPlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean

    suspend fun getPlaylistById(playlistId: Long): Playlist
    suspend fun getTracksByIds(trackIds: List<Int>): List<Track>

    suspend fun removeTrackFromPlaylist(track: Track, playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

}