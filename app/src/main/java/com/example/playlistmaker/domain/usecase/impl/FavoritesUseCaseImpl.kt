package com.example.playlistmaker.domain.usecase.impl

import com.example.playlistmaker.domain.favorites.FavoritesRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.usecase.FavoritesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesUseCaseImpl(
    private val repository: FavoritesRepository
) : FavoritesUseCase {

    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun removeTrack(track: Track) {
        repository.removeTrack(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
            .map { tracks -> tracks.reversed() }
    }
}