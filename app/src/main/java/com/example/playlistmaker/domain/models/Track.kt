package com.example.playlistmaker.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String? = "",
    val releaseDate: String? = "",
    val primaryGenreName: String? = "",
    val previewUrl: String?,
    val country: String? = ""

) : Parcelable {

    fun getReleaseYear(): String {
        return if (releaseDate?.isNotEmpty() == true && releaseDate.length >= 4) {
            releaseDate.substring(0, 4)
        } else {
            "Неизвестно"
        }
    }

    fun getFormattedTime(): String {
        return trackTime
    }

    fun getCoverArtwork(): String {
        return artworkUrl100.replace("100x100", "512x512")
    }
}