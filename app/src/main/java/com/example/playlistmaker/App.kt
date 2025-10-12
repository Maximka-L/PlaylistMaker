package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // Проверяем, включена ли системная тёмная тема
        val systemIsInDarkMode =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                    Configuration.UI_MODE_NIGHT_YES

        // Берём сохранённое значение, если нет — используем системную тему
        val isDarkTheme = prefs.getBoolean("dark_theme", systemIsInDarkMode)

        // Применяем тему
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
