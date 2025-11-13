package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.local.LocalStorage
import com.example.playlistmaker.data.local.SearchHistoryStorage
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.repository.TrackRepositoryImpl
import com.example.playlistmaker.domain.usecase.ManageSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.SearchTracksUseCase

object Creator {

    fun provideSearchTracksUseCase(context: Context): SearchTracksUseCase {
        val repository = provideRepository(context)
        return SearchTracksUseCase(repository)
    }

    fun provideManageSearchHistoryUseCase(context: Context): ManageSearchHistoryUseCase {
        val repository = provideRepository(context)
        return ManageSearchHistoryUseCase(repository)
    }

    private fun provideRepository(context: Context): TrackRepositoryImpl {
        val networkClient = NetworkClient
        val localStorage = LocalStorage(context)
        val historyStorage = SearchHistoryStorage(localStorage.sharedPrefs)
        return TrackRepositoryImpl(networkClient, historyStorage)
    }
}
