package com.example.playlistmaker
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.SettingsActivity
import com.google.android.material.button.MaterialButton


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // ваш XML с кнопками


        findViewById<MaterialButton>(R.id.searchButton).setOnClickListener {
            Toast.makeText(this, "Еще рано", Toast.LENGTH_SHORT).show()
        }


        findViewById<MaterialButton>(R.id.mediaButton).setOnClickListener {
            Toast.makeText(this, "Еще рано", Toast.LENGTH_SHORT).show()
        }

        // Кнопка "НАСТРОЙКИ" (переход на SettingsActivity)
        findViewById<MaterialButton>(R.id.settingsButton).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}