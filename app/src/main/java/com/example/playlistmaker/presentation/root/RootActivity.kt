package com.example.playlistmaker.presentation.root

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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

        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.isVisible =
                destination.id != R.id.audioPlayerFragment
        }


        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = android.graphics.Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)

            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            val isKeyboardVisible = keypadHeight > screenHeight * 0.15

            binding.bottomNavigation.isVisible =
                !isKeyboardVisible &&
                        navController.currentDestination?.id != R.id.audioPlayerFragment
        }
    }
}