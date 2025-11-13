package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.local.SearchHistoryStorage
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
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
        val sharedPrefs = context.getSharedPreferences("APP_STORAGE", Context.MODE_PRIVATE)
        val historyStorage = SearchHistoryStorage(sharedPrefs)
        val networkClient = NetworkClient
        return TrackRepositoryImpl(networkClient, historyStorage)
    }
}
