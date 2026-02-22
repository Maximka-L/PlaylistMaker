package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesUseCase {
    suspend fun addTrack(track: Track)
    suspend fun removeTrack(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>

    suspend fun isFavorite(trackId: Int): Boolean

    suspend fun toggleFavorite(track: Track): Boolean

}