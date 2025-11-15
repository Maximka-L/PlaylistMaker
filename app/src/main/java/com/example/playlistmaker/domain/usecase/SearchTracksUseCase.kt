package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TrackRepository

interface SearchTracksUseCase {
    suspend fun execute(query: String): List<Track>
}
