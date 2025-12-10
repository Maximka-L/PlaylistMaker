package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.Creator


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val getThemeUseCase = Creator.provideThemeSetter(this)

        val isDarkTheme = getThemeUseCase()

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
