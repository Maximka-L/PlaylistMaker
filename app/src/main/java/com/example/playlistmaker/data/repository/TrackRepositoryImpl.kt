package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.SearchResponse
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.local.SearchHistoryStorage
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.models.Track

import com.example.playlistmaker.domain.repository.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val localStorage: SearchHistoryStorage
) : TrackRepository {

    override suspend fun searchTracks(query: String): List<Track> = withContext(Dispatchers.IO) {
        if (!networkClient.isConnected()) {
            println("NO INTERNET")
            return@withContext emptyList()
        }

        try {
            val response = networkClient.api.searchSongs(query.trim())

            println("DTO SIZE: ${response.results.size}")
            println("FIRST TRACK: ${response.results.firstOrNull()?.trackName}")

            val mapped = response.results.map { dto -> dto.toDomain() }

            println("MAPPED SIZE: ${mapped.size}")

            return@withContext mapped

        } catch (e: Exception) {
            println("ERROR IN REPOSITORY: $e")
            return@withContext emptyList()
        }
    }

    override fun getHistory(): List<Track> = localStorage.getHistory()

    override fun addTrack(track: Track) = localStorage.addTrack(track)

    override fun clearHistory() = localStorage.clearHistory()

    private fun TrackDto.toDomain(): Track {
        val totalSeconds = (trackTimeMillis / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val formattedTime = String.format("%d:%02d", minutes, seconds)

        return Track(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTime = formattedTime,
            artworkUrl100 = artworkUrl100,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            previewUrl = previewUrl,
            country = country
        )
    }
}
