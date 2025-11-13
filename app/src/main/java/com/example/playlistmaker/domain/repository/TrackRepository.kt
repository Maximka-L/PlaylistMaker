package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Track

interface TrackRepository {


    suspend fun searchTracks(query: String): List<Track>


    fun getSearchHistory(): List<Track>
    fun addToHistory(track: Track)
    fun clearHistory()
}
