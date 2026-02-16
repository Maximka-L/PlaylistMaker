package com.example.playlistmaker.domain.usecase.impl

import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.presentation.search.SearchScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchTracksUseCaseImpl(
    private val repository: TrackRepository
) : SearchTracksUseCase {

    override fun execute(query: String): Flow<SearchScreenState> {
        return repository.searchTracks(query).map { result ->
            when (result) {
                is TrackRepository.SearchResult.Loading ->
                    SearchScreenState.Loading

                is TrackRepository.SearchResult.Success -> {
                    val tracks = result.tracks
                    if (tracks.isEmpty()) {
                        SearchScreenState.Empty(isInternetError = false)
                    } else {
                        SearchScreenState.Content(tracks)
                    }
                }

                is TrackRepository.SearchResult.Error ->
                    SearchScreenState.Empty(isInternetError = result.isInternetError)
            }
        }
    }
}
