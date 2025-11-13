package com.example.playlistmaker.utils

import com.example.playlistmaker.domain.models.Track

data class TrackDto(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String? = null,
    val releaseDate: String? = null,
    val primaryGenreName: String? = null,
    val previewUrl: String?= null,
    val country: String? = null
)

data class SearchResponse(
    val resultCount: Int,
    val results: List<TrackDto>
)

fun TrackDto.toTrack(): Track {
    val minutes = java.text.SimpleDateFormat("mm:ss", java.util.Locale.getDefault())
        .format(trackTimeMillis)
    return Track(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTime = minutes,
        artworkUrl100 = artworkUrl100,
        previewUrl = previewUrl,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country

    )
}