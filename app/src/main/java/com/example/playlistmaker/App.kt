package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    companion object {
        const val PREFS_NAME = "app_prefs"
        const val KEY_DARK_THEME = "dark_theme"
    }

    override fun onCreate() {
        super.onCreate()

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Определяем системную тему
        val systemIsDark = (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        // Если ключа ещё нет — создаём с системным значением
        if (!prefs.contains(KEY_DARK_THEME)) {
            prefs.edit().putBoolean(KEY_DARK_THEME, systemIsDark).apply()
        }

        // Применяем тему из SharedPreferences
        val isDarkTheme = prefs.getBoolean(KEY_DARK_THEME, systemIsDark)

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
