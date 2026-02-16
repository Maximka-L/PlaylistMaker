package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.presentation.search.SearchScreenState
import kotlinx.coroutines.flow.Flow

interface SearchTracksUseCase {
    fun execute(query: String): Flow<SearchScreenState>
}
