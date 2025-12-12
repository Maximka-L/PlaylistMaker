package com.example.playlistmaker.di

import com.example.playlistmaker.domain.usecase.GetThemeUseCase
import com.example.playlistmaker.domain.usecase.IGetThemeUseCase
import com.example.playlistmaker.domain.usecase.IManageSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.ISetThemeUseCase
import com.example.playlistmaker.domain.usecase.ManageSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.domain.usecase.SetThemeUseCase
import com.example.playlistmaker.domain.usecase.impl.SearchTracksUseCaseImpl
import org.koin.dsl.module

val useCaseModule = module {

    factory<SearchTracksUseCase> {
        SearchTracksUseCaseImpl(get())
    }

    factory<IManageSearchHistoryUseCase> {
        ManageSearchHistoryUseCase(get())
    }

    factory<IGetThemeUseCase> {
        GetThemeUseCase(get())
    }

    factory<ISetThemeUseCase> {
        SetThemeUseCase(get())
    }
}
