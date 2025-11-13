package com.example.playlistmaker.data.local

import android.content.Context
import android.content.SharedPreferences

class LocalStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("APP_STORAGE", Context.MODE_PRIVATE)

    val sharedPrefs: SharedPreferences
        get() = prefs

    fun saveString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }

    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
