package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.SearchResponse
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val localStorage: com.example.playlistmaker.data.local.SearchHistoryStorage
) : TrackRepository {

    override suspend fun searchTracks(query: String): List<Track> = withContext(Dispatchers.IO) {

        if (!networkClient.isConnected()){
            return@withContext emptyList()
        }

        try {
            val response: SearchResponse = networkClient.api.searchSongs(query)
            response.results.map { dto ->
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
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        } catch (e: HttpException) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun getHistory(): List<Track> = localStorage.getHistory()

    override fun addTrack(track: Track) = localStorage.addTrack(track)

    override fun clearHistory() = localStorage.clearHistory()

    private fun formatTrackTime(trackTimeMillis: Long): String {
        val minutes = (trackTimeMillis / 1000) / 60
        val seconds = (trackTimeMillis / 1000) % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}
