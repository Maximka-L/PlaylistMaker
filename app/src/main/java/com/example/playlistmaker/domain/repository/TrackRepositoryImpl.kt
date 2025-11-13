package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.data.local.SearchHistoryStorage
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.utils.toTrack

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val historyStorage: SearchHistoryStorage
) : TrackRepository {

    override suspend fun searchTracks(query: String): List<Track> {
        val response = networkClient.api.searchSongs(query)
        return response.results.map { it.toTrack() }
    }

    override fun getSearchHistory(): List<Track> {
        return historyStorage.getHistory()
    }

    override fun addToHistory(track: Track) {
        historyStorage.addTrack(track)
    }

    override fun clearHistory() {
        historyStorage.clearHistory()
    }
}
