package com.example.playlistmaker.di

import com.example.playlistmaker.domain.usecase.FavoritesUseCase
import com.example.playlistmaker.domain.usecase.PlaylistUseCase
import com.example.playlistmaker.presentation.player.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.example.playlistmaker.data.repository.PlaylistRepositoryImpl
import com.example.playlistmaker.domain.repository.PlaylistRepository

val playerModule = module {

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(
            playlistDao = get(),
            playlistTrackDao = get()
        )
    }

    viewModel {
        PlayerViewModel(
            favoritesUseCase = get<FavoritesUseCase>(),
            playlistUseCase = get<PlaylistUseCase>()
        )
    }
}