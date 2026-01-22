package com.example.playlistmaker.presentation.setting.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.usecase.IGetThemeUseCase
import com.example.playlistmaker.domain.usecase.ISetThemeUseCase

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