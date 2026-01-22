package com.example.playlistmaker.di

import com.example.playlistmaker.presentation.player.viewmodel.PlayerViewModel
import com.example.playlistmaker.presentation.search.viewmodel.SearchViewModel
import com.example.playlistmaker.presentation.setting.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

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
        PlayerViewModel(audioPlayer = get())
    }

}
