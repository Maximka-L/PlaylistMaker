package com.example.playlistmaker.domain.usecase

interface ISetThemeUseCase {
    operator fun invoke(enabled: Boolean)
}