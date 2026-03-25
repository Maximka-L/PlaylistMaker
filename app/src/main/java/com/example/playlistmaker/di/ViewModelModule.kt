package com.example.playlistmaker.di

import com.example.playlistmaker.presentation.media.favorites.FavoritesViewModel
import com.example.playlistmaker.presentation.player.viewmodel.PlayerViewModel
import com.example.playlistmaker.presentation.search.viewmodel.SearchViewModel
import com.example.playlistmaker.presentation.setting.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.example.playlistmaker.presentation.playlist.viewmodel.CreatePlaylistViewModel
import com.example.playlistmaker.presentation.media.view_model.PlayListFragmentViewModel
import com.example.playlistmaker.presentation.playlist.viewmodel.PlaylistViewModel
import com.example.playlistmaker.presentation.playlist.viewmodel.EditPlaylistViewModel

val viewModelModule = module {

    viewModel {
        SettingsViewModel(
            getTheme = get(),
            setTheme = get()
        )
    }

    viewModel {
        SearchViewModel(
            searchTracksUseCase = get(),
            manageSearchHistoryUseCase = get()
        )
    }

    viewModel {
        PlayerViewModel(
            audioPlayer = get(),
            favoritesUseCase = get(),
            playlistUseCase = get()
        )
    }

    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        CreatePlaylistViewModel(
            playlistUseCase = get()
        )
    }

    viewModel {
        PlayListFragmentViewModel(
            playlistUseCase = get()
        )
    }

    viewModel {
        PlaylistViewModel(
            playlistUseCase = get()
        )
    }

    viewModel {
        EditPlaylistViewModel(
            playlistUseCase = get())
    }

}
