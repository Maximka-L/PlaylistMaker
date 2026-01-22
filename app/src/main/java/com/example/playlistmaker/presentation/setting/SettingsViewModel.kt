package com.example.playlistmaker.presentation.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.usecase.GetThemeUseCase
import com.example.playlistmaker.domain.usecase.IGetThemeUseCase
import com.example.playlistmaker.domain.usecase.ISetThemeUseCase
import com.example.playlistmaker.domain.usecase.SetThemeUseCase

class SettingsViewModel(
    private val getTheme: IGetThemeUseCase,
    private val setTheme: ISetThemeUseCase
) : ViewModel() {

    private val _darkTheme = MutableLiveData<Boolean>()
    val darkTheme: LiveData<Boolean> = _darkTheme


    fun loadTheme() {
        _darkTheme.value = getTheme()
    }

    fun changeTheme(enabled: Boolean) {
        setTheme(enabled)
        _darkTheme.value = enabled
    }
}