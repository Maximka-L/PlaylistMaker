package com.example.playlistmaker.data.repository

import com.example.playlistmaker.domain.models.Track

interface TrackRepository {
    suspend fun searchTracks(query: String): List<Track>
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}
