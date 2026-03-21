package com.example.playlistmaker.data.mappers

import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PlaylistDbMapper {

    private val gson = Gson()

    fun toEntity(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIds = gson.toJson(playlist.trackIds),
            tracksCount = playlist.tracksCount
        )
    }

    fun fromEntity(entity: PlaylistEntity): Playlist {
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            coverPath = entity.coverPath,
            trackIds = fromJson(entity.trackIds),
            tracksCount = entity.tracksCount
        )
    }

    private fun fromJson(json: String): List<Int> {
        if (json.isBlank()) return emptyList()
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}