package com.example.playlistmaker.presentation.playlist.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.playlist.viewmodel.PlaylistEvent
import com.example.playlistmaker.presentation.playlist.viewmodel.PlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

private val ysDisplayMedium = FontFamily(Font(R.font.ys_display_medium, FontWeight.Medium))
private val ysDisplayRegular = FontFamily(Font(R.font.ys_display_regular, FontWeight.Normal))

class PlaylistFragment : Fragment() {

    private val viewModel: PlaylistViewModel by viewModel()
    private var playlistId: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        playlistId = arguments?.getLong("playlistId") ?: 0L
        viewModel.loadPlaylist(playlistId)

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Boolean>("playlist_updated")
            ?.observe(viewLifecycleOwner) { updated ->
                if (updated == true) {
                    viewModel.loadPlaylist(playlistId)
                }
            }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collect { event ->
                when (event) {
                    PlaylistEvent.ShowEmptyShareMessage -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.empty_playlist_share_message),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is PlaylistEvent.SharePlaylist -> {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, event.text)
                        }
                        startActivity(Intent.createChooser(intent, getString(R.string.share_playlist)))
                    }
                    PlaylistEvent.NavigateBack -> {
                        findNavController().navigateUp()
                    }
                }
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    PlaylistScreen(
                        viewModel = viewModel,
                        onBackClick = { findNavController().navigateUp() },
                        onTrackClick = { track ->
                            val action = PlaylistFragmentDirections.actionPlaylistFragmentToAudioPlayerFragment(track)
                            findNavController().navigate(action)
                        },
                        onDeletePlaylistConfirm = {
                            viewModel.deletePlaylist()
                        },
                        onEditPlaylistClick = { id ->
                            findNavController().navigate(
                                R.id.action_playlistFragment_to_editPlaylistFragment,
                                bundleOf("playlistId" to id)
                            )
                        },
                        onRemoveTrackConfirm = { track ->
                            viewModel.removeTrack(track)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PlaylistScreen(
    viewModel: PlaylistViewModel,
    onBackClick: () -> Unit,
    onTrackClick: (Track) -> Unit,
    onDeletePlaylistConfirm: () -> Unit,
    onEditPlaylistClick: (Long) -> Unit,
    onRemoveTrackConfirm: (Track) -> Unit
) {
    val playlist by viewModel.playlist.observeAsState()
    val tracks by viewModel.tracks.observeAsState(emptyList())
    val duration by viewModel.duration.observeAsState("0")

    val context = LocalContext.current
    var showMenuBottomSheet by remember { mutableStateOf(false) }

    playlist?.let { currentPlaylist ->
        val trackCountText = context.resources.getQuantityString(
            R.plurals.tracks_count,
            currentPlaylist.trackIds.size,
            currentPlaylist.trackIds.size
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_back),
                                contentDescription = "Назад",
                                tint = colorResource(id = R.color.text_primary)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.playlist_bg_bg))
                )
            },
            containerColor = colorResource(id = R.color.playlist_bg_bg)
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {

                    AsyncImage(
                        model = if (currentPlaylist.coverPath.isNotBlank()) File(currentPlaylist.coverPath) else null,
                        contentDescription = null,
                        placeholder = painterResource(id = R.drawable.ic_placeholder1),
                        error = painterResource(id = R.drawable.ic_placeholder1),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = currentPlaylist.name,
                            fontSize = 24.sp,
                            fontFamily = ysDisplayMedium,
                            color = colorResource(id = R.color.text_primary)
                        )
                        if (currentPlaylist.description.isNotBlank()) {
                            Text(
                                text = currentPlaylist.description,
                                fontSize = 18.sp,
                                fontFamily = ysDisplayRegular,
                                color = colorResource(id = R.color.text_primary),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Text(
                            text = context.getString(R.string.playlist_info, duration, trackCountText),
                            fontSize = 18.sp,
                            fontFamily = ysDisplayRegular,
                            color = colorResource(id = R.color.text_primary),
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            IconButton(onClick = { viewModel.onShareClicked(trackCountText) }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_share),
                                    contentDescription = "Поделиться",
                                    tint = colorResource(id = R.color.text_primary)
                                )
                            }
                            IconButton(onClick = { showMenuBottomSheet = true }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_app),
                                    contentDescription = "Еще",
                                    tint = colorResource(id = R.color.text_primary)
                                )
                            }
                        }
                    }

                    if (tracks.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.empty_playlist_tracks),
                                color = colorResource(id = R.color.text_primary_ic_black),
                                fontSize = 14.sp,
                                fontFamily = ysDisplayRegular
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(tracks) { track ->
                                com.example.playlistmaker.presentation.search.ui.TrackItem(
                                    track = track,
                                    onClick = { onTrackClick(track) },
                                    modifier = Modifier.combinedClickable(
                                        onClick = { onTrackClick(track) },
                                        onLongClick = {
                                            MaterialAlertDialogBuilder(context, R.style.WhiteDialog)
                                                .setMessage(R.string.remove_track_message)
                                                .setNegativeButton(R.string.dialog_no) { d, _ -> d.dismiss() }
                                                .setPositiveButton(R.string.dialog_yes) { d, _ ->
                                                    onRemoveTrackConfirm(track)
                                                    d.dismiss()
                                                }.show()
                                        }
                                    )
                                )
                            }
                        }
                    }
                }

                if (showMenuBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showMenuBottomSheet = false },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        containerColor = colorResource(id = R.color.background)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = if (currentPlaylist.coverPath.isNotBlank()) File(currentPlaylist.coverPath) else null,
                                    contentDescription = null,
                                    placeholder = painterResource(id = R.drawable.ic_placeholder1),
                                    error = painterResource(id = R.drawable.ic_placeholder1),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(45.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                )
                                Column(modifier = Modifier.padding(start = 8.dp)) {
                                    Text(text = currentPlaylist.name, fontSize = 16.sp, fontFamily = ysDisplayMedium, color = colorResource(id = R.color.menu_text_color))
                                    Text(text = trackCountText, fontSize = 14.sp, fontFamily = ysDisplayRegular, color = colorResource(id = R.color.menu_text_color))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Поделиться",
                                color = colorResource(id = R.color.menu_text_color),
                                fontFamily = ysDisplayRegular,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showMenuBottomSheet = false
                                        viewModel.onShareClicked(trackCountText)
                                    }
                                    .padding(vertical = 12.dp),
                                fontSize = 19.sp
                            )
                            Text(
                                text = "Редактировать информацию",
                                color = colorResource(id = R.color.menu_text_color),
                                fontFamily = ysDisplayRegular,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showMenuBottomSheet = false
                                        onEditPlaylistClick(currentPlaylist.id)
                                    }
                                    .padding(vertical = 12.dp),
                                fontSize = 19.sp
                            )
                            Text(
                                text = "Удалить плейлист",
                                color = colorResource(id = R.color.menu_text_color),
                                fontFamily = ysDisplayRegular,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showMenuBottomSheet = false
                                        MaterialAlertDialogBuilder(context, R.style.WhiteDialog)
                                            .setMessage(context.getString(R.string.delete_playlist_message, currentPlaylist.name))
                                            .setNegativeButton(R.string.delete_playlist_cancel) { d, _ -> d.dismiss() }
                                            .setPositiveButton(R.string.delete_playlist_confirm) { d, _ ->
                                                onDeletePlaylistConfirm()
                                                d.dismiss()
                                            }.show()
                                    }
                                    .padding(vertical = 12.dp),
                                fontSize = 19.sp
                            )
                        }
                    }
                }
            }
        }
    }
}