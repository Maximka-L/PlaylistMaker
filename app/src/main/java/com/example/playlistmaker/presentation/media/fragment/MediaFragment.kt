package com.example.playlistmaker.presentation.media.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.media.favorites.FavoritesViewModel
import com.example.playlistmaker.presentation.media.ui.MediaScreen
import com.example.playlistmaker.presentation.media.view_model.PlayListFragmentViewModel
import com.example.playlistmaker.presentation.playlist.fragment.PlaylistFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragment : Fragment() {

    private val favoritesViewModel: FavoritesViewModel by viewModel()
    private val playListsViewModel: PlayListFragmentViewModel by viewModel()

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                val pagerState = rememberPagerState(pageCount = { 2 })
                val coroutineScope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }

                val messageData = findNavController().currentBackStackEntry
                    ?.savedStateHandle
                    ?.getLiveData<String>("playlist_created_message")
                    ?.observeAsState()

                LaunchedEffect(messageData?.value) {
                    messageData?.value?.let { message ->
                        snackbarHostState.showSnackbar(
                            message = message,
                            duration = SnackbarDuration.Short
                        )
                        findNavController().currentBackStackEntry
                            ?.savedStateHandle
                            ?.remove<String>("playlist_created_message")
                    }
                }

                MediaScreen(
                    pagerState = pagerState,
                    coroutineScope = coroutineScope,
                    snackbarHostState = snackbarHostState,
                    favoritesViewModel = favoritesViewModel,
                    playListsViewModel = playListsViewModel,
                    onTrackClick = { track -> openPlayer(track) },
                    onCreatePlaylistClick = { openCreatePlaylist() },
                    onPlaylistClick = { playlistId -> openPlaylist(playlistId) }
                )
            }
        }
    }

    private fun openPlayer(track: Track) {
        val bundle = Bundle().apply { putParcelable("track", track) }
        findNavController().navigate(
            R.id.audioPlayerFragment,
            bundle
        )
    }

    private fun openCreatePlaylist() {
        findNavController().navigate(R.id.createPlaylistFragment)
    }

    private fun openPlaylist(playlistId: Long) {
        findNavController().navigate(
            R.id.action_mediaFragment_to_playlistFragment,
            bundleOf(PlaylistFragment.PLAYLIST_ID_KEY to playlistId)
        )
    }
}