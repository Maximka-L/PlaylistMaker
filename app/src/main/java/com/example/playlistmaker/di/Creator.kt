package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.local.SearchHistoryStorage
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.data.settings.SettingsRepositoryImpl
import com.example.playlistmaker.domain.usecase.GetThemeUseCase
import com.example.playlistmaker.domain.usecase.ManageSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.domain.usecase.SetThemeUseCase
import com.example.playlistmaker.domain.usecase.impl.SearchTracksUseCaseImpl
import com.example.playlistmaker.presentation.setting.SettingsViewModel

object Creator {

    fun provideSearchTracksUseCase(context: Context): SearchTracksUseCase {
        val repository = provideRepository(context)
        return SearchTracksUseCaseImpl(repository)
    }

    fun provideManageSearchHistoryUseCase(context: Context): ManageSearchHistoryUseCase {
        val repository = provideRepository(context)
        return ManageSearchHistoryUseCase(repository)
    }

    private fun provideRepository(context: Context): TrackRepositoryImpl {
        val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val historyStorage = SearchHistoryStorage(sharedPrefs)
        val networkClient = NetworkClient(context)
        return TrackRepositoryImpl(networkClient, historyStorage)
    }

    fun provideSettingsViewModel(context: Context): SettingsViewModel {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val repo = SettingsRepositoryImpl(prefs)

        return SettingsViewModel(
            GetThemeUseCase(repo),
            SetThemeUseCase(repo)
        )
    }

    fun provideThemeSetter(context: Context): GetThemeUseCase {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val repo = SettingsRepositoryImpl(prefs)
        return GetThemeUseCase(repo)
    }


}