package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {

    fun searchTracks(query: String): Flow<SearchResult>

    suspend fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()

    sealed interface SearchResult {
        data object Loading : SearchResult
        data class Success(val tracks: List<Track>) : SearchResult
        data class Error(val isInternetError: Boolean) : SearchResult
    }
}