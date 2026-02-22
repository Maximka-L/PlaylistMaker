package com.example.playlistmaker.domain.usecase.impl

import com.example.playlistmaker.domain.favorites.FavoritesRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.usecase.FavoritesUseCase
import kotlinx.coroutines.flow.Flow

class FavoritesUseCaseImpl(
    private val favoritesRepository: FavoritesRepository
) : FavoritesUseCase {

    override suspend fun addTrack(track: Track) {
        favoritesRepository.addTrack(track)
    }

    override suspend fun removeTrack(track: Track) {
        favoritesRepository.removeTrack(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoritesRepository.getFavoriteTracks()
    }

    override suspend fun isFavorite(trackId: Int): Boolean {
        return favoritesRepository.isFavorite(trackId)
    }


    override suspend fun toggleFavorite(track: Track): Boolean {
        val currentlyFavorite = favoritesRepository.isFavorite(track.trackId)
        return if (currentlyFavorite) {
            favoritesRepository.removeTrack(track)
            false
        } else {
            favoritesRepository.addTrack(track)
            true
        }
    }
}