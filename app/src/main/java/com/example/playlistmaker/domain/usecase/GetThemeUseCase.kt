package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.repository.SettingsRepository

class GetThemeUseCase(private val repo: SettingsRepository) : IGetThemeUseCase {
    override operator fun invoke(): Boolean = repo.isDarkTheme()
}
