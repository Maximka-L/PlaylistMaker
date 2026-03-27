package com.example.playlistmaker.presentation.playlist.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.playlist.viewmodel.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import androidx.navigation.fragment.findNavController

class EditPlaylistFragment : CreatePlaylistFragment() {

    override val viewModel: EditPlaylistViewModel by viewModel()

    private var playlistId: Long = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playlistId = arguments?.getLong("playlistId") ?: 0L
        viewModel.loadPlaylist(playlistId)

        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.title = getString(R.string.edit_playlist_title)
        binding.createButton.text = getString(R.string.save)

        viewModel.originalPlaylist.observe(viewLifecycleOwner) { playlist ->
            if (playlist != null) {
                binding.playlistNameEditText.setText(playlist.name)
                binding.playlistNameEditText.isActivated = playlist.name.isNotBlank()
                binding.playlistDescriptionEditText.setText(playlist.description)
                binding.playlistDescriptionEditText.isActivated = playlist.description.isNotBlank()
            }
        }
    }

    override fun handleBackAction() {
        findNavController().popBackStack()
    }

    override fun handleSaveAction() {
        val name = binding.playlistNameEditText.text.toString().trim()
        val description = binding.playlistDescriptionEditText.text.toString().trim()

        if (name.isBlank()) return

        val coverPath = viewModel.coverUri.value?.let { uriString ->
            val file = File(uriString)
            if (file.exists()) uriString
            else saveImageToPrivateStorage(Uri.parse(uriString))
        } ?: viewModel.getOriginalPlaylist()?.coverPath ?: ""

        viewModel.updatePlaylist(name, description, coverPath)

        findNavController().previousBackStackEntry
            ?.savedStateHandle
            ?.set("playlist_updated", true)

        findNavController().popBackStack()
    }
}