package com.example.playlistmaker.presentation.main

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.presentation.media.MediaActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.search.SearchActivity
import com.example.playlistmaker.presentation.setting.SettingsActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_theme", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout)) { view, insets ->
            val statusBar =
                insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        setupToolbar()
        setupButtons()
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.toolbar).apply {
            title = try {
                getString(R.string.app_name)
            } catch (e: Resources.NotFoundException) {
                "Playlist Maker"
            }
        }
    }

    private fun setupButtons() {
        findViewById<MaterialButton>(R.id.searchButton).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.mediaButton).setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.settingsButton).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}