package com.example.playlistmaker.presentation.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.di.Creator

class SearchViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            val manageSearchHistoryUseCase =
                Creator.provideManageSearchHistoryUseCase(context)
            val searchTracksUseCase =
                Creator.provideSearchTracksUseCase(context)

            return SearchViewModel(
                searchTracksUseCase = searchTracksUseCase,
                manageSearchHistoryUseCase = manageSearchHistoryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class $modelClass")
    }
}
