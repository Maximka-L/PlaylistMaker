package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.mappers.FavoriteTrackDbMapper
import com.example.playlistmaker.domain.favorites.FavoritesRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val database: AppDatabase
) : FavoritesRepository {

    private val dao = database.favoriteTrackDao()

    override suspend fun addTrack(track: Track) {
        dao.insertTrack(FavoriteTrackDbMapper.toEntity(track))
    }

    override suspend fun removeTrack(track: Track) {
        dao.deleteTrack(FavoriteTrackDbMapper.toEntity(track))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return dao.getAllFavoriteTracks()
            .map { entities -> entities.map(FavoriteTrackDbMapper::fromEntity) }
    }
}