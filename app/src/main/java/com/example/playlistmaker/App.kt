package com.example.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.useCaseModule
import com.example.playlistmaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import com.example.playlistmaker.di.playerModule
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                repositoryModule,
                useCaseModule,
                viewModelModule,
                playerModule
            )
        }


        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_theme", false)

        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
