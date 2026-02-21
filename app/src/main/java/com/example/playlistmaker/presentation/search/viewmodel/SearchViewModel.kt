package com.example.playlistmaker.presentation.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.usecase.IManageSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.presentation.common.Event
import com.example.playlistmaker.presentation.search.SearchScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val CLICK_DEBOUNCE_DELAY = 800L
private const val SEARCH_DEBOUNCE_DELAY = 2000L

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val manageSearchHistoryUseCase: IManageSearchHistoryUseCase
) : ViewModel() {

    private val _state = MutableLiveData<SearchScreenState>()
    val state: LiveData<SearchScreenState> = _state

    private val _openTrackEvent = MutableLiveData<Event<Track>>()
    val openTrackEvent: LiveData<Event<Track>> = _openTrackEvent

    private var searchDebounceJob: Job? = null
    private var searchCollectJob: Job? = null
    private var clickDebounceJob: Job? = null

    private var currentQuery: String = ""
    private var lastTracks: List<Track>? = null

    init {
        restoreState()
    }

    private fun restoreState() {
        if (lastTracks != null) {
            _state.value = SearchScreenState.Content(lastTracks!!)
            return
        }

        viewModelScope.launch {
            val history = manageSearchHistoryUseCase.getHistory()
            _state.value = SearchScreenState.History(history)
        }
    }

    fun onSearchTextChanged(text: String) {
        val newQuery = text.trim()
        if (newQuery == currentQuery) return
        currentQuery = newQuery

        searchDebounceJob?.cancel()

        if (currentQuery.isEmpty()) {
            lastTracks = null
            searchCollectJob?.cancel()

            viewModelScope.launch {
                _state.value = SearchScreenState.History(manageSearchHistoryUseCase.getHistory())
            }
            return
        }

        searchDebounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            performSearch(currentQuery)
        }
    }

    fun onSearchButtonClicked() {
        if (currentQuery.isEmpty()) return
        searchDebounceJob?.cancel()
        performSearch(currentQuery)
    }

    private fun performSearch(query: String) {
        searchCollectJob?.cancel()

        searchCollectJob = viewModelScope.launch {
            searchTracksUseCase.execute(query).collect { newState ->
                _state.value = newState

                when (newState) {
                    is SearchScreenState.Content -> lastTracks = newState.tracks
                    is SearchScreenState.Empty -> lastTracks = emptyList()
                    else -> Unit
                }
            }
        }
    }

    fun onTrackClicked(track: Track) {
        if (clickDebounceJob?.isActive == true) return

        clickDebounceJob = viewModelScope.launch {
            manageSearchHistoryUseCase.addTrack(track)
            _openTrackEvent.value = Event(track)
            delay(CLICK_DEBOUNCE_DELAY)
        }
    }
    fun onResume() {
        if (currentQuery.isBlank()) {
            viewModelScope.launch {
                _state.value = SearchScreenState.History(manageSearchHistoryUseCase.getHistory())
            }
        } else {
            performSearch(currentQuery)
        }
    }

    fun onClearHistoryClicked() {
        manageSearchHistoryUseCase.clearHistory()
        _state.value = SearchScreenState.History(emptyList())
    }
}