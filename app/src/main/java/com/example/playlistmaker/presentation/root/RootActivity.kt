package com.example.playlistmaker.presentation.root

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                ?.findNavController() ?: return

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.searchFragment -> {
                    navController.navigate(
                        R.id.searchFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(navController.graph.startDestinationId, false)
                            .setLaunchSingleTop(true)
                            .build()
                    )
                    true
                }

                R.id.mediaFragment -> {
                    navController.navigate(
                        R.id.mediaFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(navController.graph.startDestinationId, false)
                            .setLaunchSingleTop(true)
                            .build()
                    )
                    true
                }

                R.id.settingsFragment -> {
                    navController.navigate(
                        R.id.settingsFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(navController.graph.startDestinationId, false)
                            .setLaunchSingleTop(true)
                            .build()
                    )
                    true
                }

                else -> false
            }
        }
    }
}