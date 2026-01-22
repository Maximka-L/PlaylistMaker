package com.example.playlistmaker.presentation.setting.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.setting.viewmodel.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val darkSwitch = view.findViewById<SwitchMaterial>(R.id.switch_dark)

        viewModel.darkTheme.observe(viewLifecycleOwner) { enabled ->
            if (darkSwitch.isChecked != enabled) {
                darkSwitch.isChecked = enabled
            }

            AppCompatDelegate.setDefaultNightMode(
                if (enabled) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        darkSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.changeTheme(checked)
        }

        viewModel.loadTheme()

        // Поделиться приложением
        view.findViewById<LinearLayout>(R.id.row_share).setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.course_link))
            }
            startActivity(Intent.createChooser(intent, getString(R.string.share_app)))
        }

        // Поддержка
        view.findViewById<LinearLayout>(R.id.row_support).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_body))
            }
            startActivity(intent)
        }

        // Пользовательское соглашение
        view.findViewById<LinearLayout>(R.id.row_terms).setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.agreement_link))
                )
            )
        }
    }
}