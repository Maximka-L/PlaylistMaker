package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.repository.SettingsRepository

class SetThemeUseCase(private val repo: SettingsRepository) {
    operator fun invoke(enabled: Boolean) = repo.setDarkTheme(enabled)
}
