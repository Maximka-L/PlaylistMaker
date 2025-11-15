package com.example.playlistmaker.domain.usecase.impl

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.domain.usecase.SearchTracksUseCase

class SearchTracksUseCaseImpl(
    private val repository: TrackRepository
) : SearchTracksUseCase {

    override suspend fun execute(query: String): List<Track> {
        return repository.searchTracks(query)
    }
}