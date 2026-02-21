package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.FavoriteTrackDao
import com.example.playlistmaker.data.dto.SearchResponse
import com.example.playlistmaker.data.local.SearchHistoryStorage
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val localStorage: SearchHistoryStorage,
    private val favoriteTrackDao: FavoriteTrackDao
) : TrackRepository {

    override fun searchTracks(query: String): Flow<TrackRepository.SearchResult> = flow {
        emit(TrackRepository.SearchResult.Loading)

        if (!networkClient.isConnected()) {
            emit(TrackRepository.SearchResult.Error(isInternetError = true))
            return@flow
        }

        try {
            val response: SearchResponse = networkClient.searchSongs(query)

            val tracks = response.results.map { dto ->
                Track(
                    trackId = dto.trackId,
                    trackName = dto.trackName,
                    artistName = dto.artistName,
                    trackTime = formatTrackTime(dto.trackTimeMillis),
                    artworkUrl100 = dto.artworkUrl100,
                    collectionName = dto.collectionName,
                    releaseDate = dto.releaseDate,
                    primaryGenreName = dto.primaryGenreName,
                    country = dto.country,
                    previewUrl = dto.previewUrl
                )
            }

            val favoriteIds = favoriteTrackDao.getFavoriteTrackIdsOnce().toSet()
            tracks.forEach { track ->
                track.isFavorite = track.trackId in favoriteIds
            }

            emit(TrackRepository.SearchResult.Success(tracks))
        } catch (e: IOException) {
            emit(TrackRepository.SearchResult.Error(isInternetError = true))
        } catch (e: HttpException) {
            emit(TrackRepository.SearchResult.Error(isInternetError = false))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getHistory(): List<Track> {
        val history = localStorage.getHistory().toMutableList()


        val favoriteIds = favoriteTrackDao.getFavoriteTrackIdsOnce().toSet()
        history.forEach { track ->
            track.isFavorite = track.trackId in favoriteIds
        }

        return history
    }

    override fun addTrack(track: Track) = localStorage.addTrack(track)

    override fun clearHistory() = localStorage.clearHistory()

    private fun formatTrackTime(trackTimeMillis: Long): String {
        val minutes = (trackTimeMillis / 1000) / 60
        val seconds = (trackTimeMillis / 1000) % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}