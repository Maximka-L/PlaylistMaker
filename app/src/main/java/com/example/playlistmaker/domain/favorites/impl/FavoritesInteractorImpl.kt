package com.example.playlistmaker.domain.favorites.impl

import com.example.playlistmaker.domain.favorites.FavoritesRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesInteractorImpl (

    private val repository: FavoritesRepository

    ): FavoritesRepository {
    override suspend fun addTrack(track: Track) {
       repository.addTrack(track)
    }

    override suspend fun removeTrack(track: Track) {
        repository.removeTrack(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
            .map {list -> list.reversed() }
    }

    override suspend fun isFavorite(trackId: Int): Boolean {
        return repository.isFavorite(trackId)
    }
}