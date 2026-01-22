package com.example.playlistmaker.presentation.root

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
class RootActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRootBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                ?.findNavController() ?: return

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.isVisible =
                destination.id != R.id.audioPlayerFragment
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

            binding.bottomNavigation.isVisible =
                !imeVisible && navController.currentDestination?.id != R.id.audioPlayerFragment

            insets
        }

        binding.bottomNavigation.setupWithNavController(navController)
    }
}
