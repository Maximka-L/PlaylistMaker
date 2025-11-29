package com.example.playlistmaker.data.settings

import android.content.SharedPreferences
import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val sharedPrefs: SharedPreferences) : SettingsRepository {

    override fun isDarkTheme(): Boolean =
        sharedPrefs.getBoolean("dark_theme", false)

    override fun setDarkTheme(enabled: Boolean) {
        sharedPrefs.edit().putBoolean("dark_theme", enabled).apply()
    }
}