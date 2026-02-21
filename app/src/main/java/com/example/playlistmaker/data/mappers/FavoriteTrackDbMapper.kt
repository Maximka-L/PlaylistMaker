package com.example.playlistmaker.data.mappers

import com.example.playlistmaker.data.db.FavoriteTrackEntity
import com.example.playlistmaker.domain.models.Track

object FavoriteTrackDbMapper {

    fun toEntity(track: Track): FavoriteTrackEntity {
        return FavoriteTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            previewUrl = track.previewUrl,
            country = track.country
        )
    }

    fun fromEntity(entity: FavoriteTrackEntity): Track {
        return Track(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTime = entity.trackTime,
            artworkUrl100 = entity.artworkUrl100,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            primaryGenreName = entity.primaryGenreName,
            previewUrl = entity.previewUrl,
            country = entity.country,
            isFavorite = true
        )
    }
}