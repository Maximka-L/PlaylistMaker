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
        } else {
            showHistory()
        }
    }

    private fun showHistory() {
        _state.value = SearchScreenState.History(
            manageSearchHistoryUseCase.getHistory()
        )
    }

    fun onSearchTextChanged(text: String) {
        currentQuery = text.trim()

        if (currentQuery.isEmpty()) {
            cancelSearch()
            showHistory()
        }
    }

    fun onSearchButtonClicked() {
        if (currentQuery.isEmpty()) return

        cancelSearch()
        searchJob = viewModelScope.launch {
            _state.value = SearchScreenState.Loading
            try {
                val tracks = searchTracksUseCase.execute(currentQuery)
                _state.value =
                    if (tracks.isEmpty())
                        SearchScreenState.Empty(false)
                    else
                        SearchScreenState.Content(tracks)
            } catch (e: IOException) {
                _state.value = SearchScreenState.Empty(true)
            }
        }
    }

    fun cancelSearch() {
        searchJob?.cancel()
        searchJob = null
    }

    fun onClearSearchClicked() {
        currentQuery = ""
        cancelSearch()
        showHistory()
    }

    fun onClearHistoryClicked() {
        manageSearchHistoryUseCase.clearHistory()
        showHistory()
    }

    fun onTrackClicked(track: Track) {
        val now = System.currentTimeMillis()
        if (now - lastClickTime < 800L) return
        lastClickTime = now

        manageSearchHistoryUseCase.addTrack(track)
        _openTrackEvent.value = Event(track)
    }
}
