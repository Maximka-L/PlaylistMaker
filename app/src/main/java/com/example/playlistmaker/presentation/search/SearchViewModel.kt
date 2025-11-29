package com.example.playlistmaker.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.usecase.ManageSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.SearchTracksUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracks: SearchTracksUseCase,
    private val history: ManageSearchHistoryUseCase
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state

    private var debounceJob: Job? = null

    fun search(query: String) {
        if (query.isEmpty()) {
            showHistory()
            return
        }

        _state.value = SearchState.Loading

        viewModelScope.launch {
            val result = searchTracks.execute(query)

            if (result.isEmpty())
                _state.value = SearchState.Empty
            else
                _state.value = SearchState.Content(result)
        }
    }

    fun showHistory() {
        _state.value = SearchState.History(history.getHistory())

    }

    fun clearHistory() {
        history.clearHistory()
        showHistory()
    }

    fun addToHistory(track: Track) {
        history.addTrack(track)
    }

    fun getHistory(): List<Track> = history.getHistory()

    sealed interface SearchState {
        object Loading : SearchState
        object Empty : SearchState
        data class Content(val data: List<Track>) : SearchState
        data class History(val data: List<Track>) : SearchState
        data class Error(val message: String) : SearchState
    }

}