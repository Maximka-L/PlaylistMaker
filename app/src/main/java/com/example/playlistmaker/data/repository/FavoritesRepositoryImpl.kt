package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.FavoriteTrackDao
import com.example.playlistmaker.data.mappers.FavoriteTrackDbMapper
import com.example.playlistmaker.domain.favorites.FavoritesRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val favoriteTrackDao: FavoriteTrackDao
) : FavoritesRepository {

    override suspend fun addTrack(track: Track) {
        favoriteTrackDao.insertTrack(FavoriteTrackDbMapper.toEntity(track))
    }

    override suspend fun removeTrack(track: Track) {
        favoriteTrackDao.deleteTrack(FavoriteTrackDbMapper.toEntity(track))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTrackDao.getAllFavoriteTracks()
            .distinctUntilChanged()
            .map { entities -> entities.map(FavoriteTrackDbMapper::fromEntity) }
    }

    override suspend fun isFavorite(trackId: Int): Boolean {
        return favoriteTrackDao.isFavoriteOnce(trackId)
    }
}