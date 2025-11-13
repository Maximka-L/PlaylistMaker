package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TrackRepository

class ManageSearchHistoryUseCase(private val repository: TrackRepository) {

    fun getHistory(): List<Track> = repository.getSearchHistory()

    fun addTrack(track: Track) = repository.addToHistory(track)

    fun clearHistory() = repository.clearHistory()
}
