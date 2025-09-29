package com.example.playlistmaker.utils

import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {
    @GET("search?entity=song")
    suspend fun searchSongs(@Query("term") term: String): SearchResponse
}
