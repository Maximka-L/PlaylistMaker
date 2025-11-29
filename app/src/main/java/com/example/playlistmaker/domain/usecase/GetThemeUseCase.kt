package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.repository.SettingsRepository

class GetThemeUseCase(private val repo: SettingsRepository) {
    operator fun invoke(): Boolean = repo.isDarkTheme()
}
