package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    private val gson = Gson()
    private val key = "search_history2"

    fun getHistory(): ArrayList<Track> {
        val json = sharedPrefs.getString(key, null) ?: return arrayListOf()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()

        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)

        if (history.size > 10) {
            history.removeAt(history.size - 1)
        }

        saveHistory(ArrayList(history))
    }


    fun clearHistory() {
        sharedPrefs.edit().remove(key).apply()
    }

    private fun saveHistory(history: ArrayList<Track>) {
        val json = gson.toJson(history)
        sharedPrefs.edit().putString(key, json).apply()
    }
}
