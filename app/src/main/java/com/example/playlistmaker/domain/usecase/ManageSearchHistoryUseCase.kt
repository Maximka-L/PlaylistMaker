package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TrackRepository

interface IManageSearchHistoryUseCase {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}

class ManageSearchHistoryUseCase(
    private val repository: TrackRepository
) : IManageSearchHistoryUseCase {

    override fun getHistory() = repository.getHistory()
    override fun addTrack(track: Track) = repository.addTrack(track)
    override fun clearHistory() = repository.clearHistory()
}