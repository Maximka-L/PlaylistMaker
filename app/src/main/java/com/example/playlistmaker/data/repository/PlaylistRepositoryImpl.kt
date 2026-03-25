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

    override suspend fun getPlaylistById(playlistId: Long): Playlist {
        return PlaylistDbMapper.fromEntity(
            playlistDao.getPlaylistById(playlistId)
        )
    }

    override suspend fun getTracksByIds(trackIds: List<Int>): List<Track> {
        if (trackIds.isEmpty()) return emptyList()

        val allTracks = playlistTrackDao.getAllTracks()
        val trackMap = allTracks.associateBy { it.trackId }

        return trackIds.reversed().mapNotNull { id ->
            trackMap[id]?.let { PlaylistTrackDbMapper.fromEntity(it) }
        }
    }

    override suspend fun removeTrackFromPlaylist(track: Track, playlist: Playlist) {
        val updatedTrackIds = playlist.trackIds.toMutableList().apply {
            remove(track.trackId)
        }

        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            tracksCount = updatedTrackIds.size
        )

        playlistDao.updatePlayList(PlaylistDbMapper.toEntity(updatedPlaylist))

        deleteTrackIfUnused(track.trackId)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylistById(playlist.id)

        playlist.trackIds.forEach { trackId ->
            deleteTrackIfUnused(trackId)
        }
    }

    private suspend fun deleteTrackIfUnused(trackId: Int) {
        val playlists = playlistDao.getPlaylistsOnce()
            .map(PlaylistDbMapper::fromEntity)

        val isUsedSomewhere = playlists.any { playlist ->
            trackId in playlist.trackIds
        }

        if (!isUsedSomewhere) {
            playlistTrackDao.deleteTrackById(trackId)
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlayList(PlaylistDbMapper.toEntity(playlist))
    }
}