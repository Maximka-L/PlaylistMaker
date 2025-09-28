package com.example.playlistmaker.utils

import com.example.playlistmaker.Track

data class TrackDto(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String
)

data class SearchResponse(
    val resultCount: Int,
    val results: List<TrackDto>
)

fun TrackDto.toTrack(): Track {
    val minutes = java.text.SimpleDateFormat("mm:ss", java.util.Locale.getDefault())
        .format(trackTimeMillis)
    return Track(
        trackName = trackName,
        artistName = artistName,
        trackTime = minutes,
        artworkUrl100 = artworkUrl100
    )
}
