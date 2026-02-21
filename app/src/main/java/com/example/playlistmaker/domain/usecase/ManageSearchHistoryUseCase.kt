package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TrackRepository

interface IManageSearchHistoryUseCase {
    suspend fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}

class ManageSearchHistoryUseCase(
    private val repository: TrackRepository
) : IManageSearchHistoryUseCase {

    override suspend fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun addTrack(track: Track) = repository.addTrack(track)

    override fun clearHistory() = repository.clearHistory()
}