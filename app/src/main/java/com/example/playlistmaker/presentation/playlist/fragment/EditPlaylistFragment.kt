package com.example.playlistmaker.presentation.playlist.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.playlist.viewmodel.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class EditPlaylistFragment : CreatePlaylistFragment() {

    override val viewModel: EditPlaylistViewModel by viewModel()
    private var playlistId: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        playlistId = arguments?.getLong("playlistId") ?: 0L
        viewModel.loadPlaylist(playlistId)

        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    val originalPlaylist by viewModel.originalPlaylist.observeAsState()
                    val coverUriState by viewModel.coverUri.observeAsState(null)
                    val coverUri = coverUriState ?: ""

                    PlaylistFormScreen(
                        screenTitle = stringResource(id = R.string.edit_playlist_title),
                        buttonText = stringResource(id = R.string.save),
                        initialName = originalPlaylist?.name ?: "",
                        initialDescription = originalPlaylist?.description ?: "",
                        initialCoverUri = coverUri.ifBlank { originalPlaylist?.coverPath ?: "" },
                        onBack = { findNavController().popBackStack() },
                        onCoverUriChanged = { viewModel.setCoverUri(it) },
                        onSave = { name, description, finalUri ->
                            val coverPath = if (finalUri.isNotBlank()) {
                                val file = File(finalUri)
                                if (file.exists()) finalUri else saveImageToPrivateStorage(Uri.parse(finalUri))
                            } else viewModel.getOriginalPlaylist()?.coverPath ?: ""

                            viewModel.updatePlaylist(name, description, coverPath)

                            findNavController().previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("playlist_updated", true)
                            findNavController().popBackStack()
                        }
                    )
                }
            }
        }
    }

    override fun onBackClicked(coverUri: String) {
        findNavController().popBackStack()
    }
}