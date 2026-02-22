package com.example.playlistmaker.di

import com.example.playlistmaker.data.repository.FavoritesRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.data.settings.SettingsRepositoryImpl
import com.example.playlistmaker.domain.favorites.FavoritesRepository
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.repository.TrackRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<TrackRepository> {
        TrackRepositoryImpl(
            networkClient = get(),
            localStorage = get()
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(
            favoriteTrackDao = get()
        )
    }
}