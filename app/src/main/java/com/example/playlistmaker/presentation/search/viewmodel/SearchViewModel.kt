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
import java.io.IOException

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

    private var searchJob: Job? = null
    private var lastClickTime = 0L
    private var currentQuery: String = ""

    fun onScreenOpened(isNetworkAvailable: Boolean) {
        if (!isNetworkAvailable) {
            _state.value = SearchScreenState.Empty(isInternetError = true)
            return
        }

        if (currentQuery.isEmpty()) {
            showHistory()
        }
    }

    private fun showHistory() {
        val history = manageSearchHistoryUseCase.getHistory()
        _state.value = SearchScreenState.History(history)
    }

    fun onSearchTextChanged(text: String) {
        currentQuery = text.trim()

        if (currentQuery.isEmpty()) {
            searchJob?.cancel()
            showHistory()
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            performSearch(currentQuery)
        }
    }

    fun onSearchButtonClicked() {
        if (currentQuery.isNotEmpty()) {
            searchJob?.cancel()
            performSearch(currentQuery)
        }
    }

    fun onClearSearchClicked() {
        currentQuery = ""
        searchJob?.cancel()
        showHistory()
    }

    fun onClearHistoryClicked() {
        manageSearchHistoryUseCase.clearHistory()
        showHistory()
    }

    fun onTrackClicked(track: Track) {
        val now = System.currentTimeMillis()
        if (now - lastClickTime < CLICK_DEBOUNCE_DELAY) return
        lastClickTime = now

        manageSearchHistoryUseCase.addTrack(track)
        _openTrackEvent.value = Event(track)
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _state.value = SearchScreenState.Loading
            try {
                val tracks = searchTracksUseCase.execute(query)

                _state.value = if (tracks.isEmpty()) {
                    SearchScreenState.Empty(isInternetError = false)
                } else {
                    SearchScreenState.Content(tracks)
                }
            } catch (e: IOException) {
                _state.value = SearchScreenState.Empty(isInternetError = true)
            }
        }
    }
}
