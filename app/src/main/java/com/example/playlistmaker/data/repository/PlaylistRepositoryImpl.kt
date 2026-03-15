package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.PlaylistDao
import com.example.playlistmaker.data.db.PlaylistTrackDao
import com.example.playlistmaker.data.mappers.PlaylistDbMapper
import com.example.playlistmaker.data.mappers.PlaylistTrackDbMapper
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(PlaylistDbMapper.toEntity(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists()
            .distinctUntilChanged()
            .map { playlists ->
                playlists.map(PlaylistDbMapper::fromEntity)
            }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {
        if (track.trackId in playlist.trackIds) return false

        playlistTrackDao.insertTrack(PlaylistTrackDbMapper.toEntity(track))

        val updatedTrackIds = playlist.trackIds + track.trackId
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            tracksCount = updatedTrackIds.size
        )

        playlistDao.updatePlayList(PlaylistDbMapper.toEntity(updatedPlaylist))
        return true
    }
}