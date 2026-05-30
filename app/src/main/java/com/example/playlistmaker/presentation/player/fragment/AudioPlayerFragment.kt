package com.example.playlistmaker.presentation.player.fragment

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.service.AudioPlayerService
import com.example.playlistmaker.presentation.player.viewmodel.PlayerViewModel
import com.example.playlistmaker.presentation.player.viewmodel.PlaylistAddStatus
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

private val ysDisplayMedium = FontFamily(Font(R.font.ys_display_medium, FontWeight.Medium))
private val ysDisplayBold = FontFamily(Font(R.font.ys_display_medium, FontWeight.Bold))

class AudioPlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModel()
    private var currentTrack: Track? = null
    private var serviceConnection: ServiceConnection? = null
    private var isBound = false

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        currentTrack = arguments?.getParcelable("track")

        if (currentTrack == null) {
            findNavController().popBackStack()
            return ComposeView(requireContext())
        }

        viewModel.setTrack(currentTrack!!)
        bindPlayerService()

        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    AudioPlayerScreen(
                        viewModel = viewModel,
                        track = currentTrack!!,
                        onBackClick = { findNavController().popBackStack() },
                        onNewPlaylistClick = {
                            findNavController().navigate(R.id.action_audioPlayerFragment_to_createPlaylistFragment)
                        },
                        onToastMessage = { message -> showCustomToast(message) }
                    )
                }
            }
        }
    }

    private fun bindPlayerService() {
        val intent = Intent(requireContext(), AudioPlayerService::class.java).apply {
            putExtra(AudioPlayerService.EXTRA_ARTIST, currentTrack?.artistName ?: "")
            putExtra(AudioPlayerService.EXTRA_TRACK, currentTrack?.trackName ?: "")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as AudioPlayerService.PlayerBinder
                viewModel.onServiceConnected(binder.getService())
                currentTrack?.previewUrl?.let { url ->
                    viewModel.prepare(
                        url = url,
                        artistName = currentTrack?.artistName ?: "",
                        trackName = currentTrack?.trackName ?: ""
                    )
                }
                isBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isBound = false
            }
        }

        requireContext().bindService(intent, serviceConnection!!, Context.BIND_AUTO_CREATE)
    }

    override fun onStart() {
        super.onStart()
        viewModel.onAppInForeground()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onAppInBackground()
    }

    override fun onDestroyView() {
        viewModel.stopPlayback()
        if (isBound) {
            serviceConnection?.let { requireContext().unbindService(it) }
            isBound = false
        }
        super.onDestroyView()
    }

    private fun showCustomToast(message: String) {
        val layout = layoutInflater.inflate(R.layout.toast_playlist, null)
        val textView = layout.findViewById<TextView>(R.id.toast_text)
        textView.text = message

        val toast = Toast(requireContext())
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 32)
        toast.show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerScreen(
    viewModel: PlayerViewModel,
    track: Track,
    onBackClick: () -> Unit,
    onNewPlaylistClick: () -> Unit,
    onToastMessage: (String) -> Unit
) {
    val currentTime by viewModel.time.observeAsState("0:00")
    val isPlaying by viewModel.isPlayingLive.observeAsState(false)
    val isFavorite by viewModel.isFavorite.observeAsState(false)
    val playlists by viewModel.playlists.observeAsState(emptyList())
    val playlistAddStatus by viewModel.playlistAddStatus.observeAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(playlistAddStatus) {
        playlistAddStatus?.let { status ->
            when (status) {
                is PlaylistAddStatus.Added -> {
                    showBottomSheet = false
                    onToastMessage(context.getString(R.string.added_to_playlist, status.playlistName))
                    viewModel.clearPlaylistAddStatus()
                }
                is PlaylistAddStatus.AlreadyExists -> {
                    onToastMessage(context.getString(R.string.track_already_in_playlist, status.playlistName))
                    viewModel.clearPlaylistAddStatus()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Назад",
                            tint = colorResource(id = R.color.icon_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.background)
                )
            )
        },
        containerColor = colorResource(id = R.color.background)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp)
            ) {
                AsyncImage(
                    model = track.getCoverArtwork(),
                    contentDescription = null,
                    placeholder = painterResource(id = R.drawable.placeholder_cover),
                    error = painterResource(id = R.drawable.placeholder_cover),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Text(
                    text = track.trackName,
                    fontSize = 22.sp,
                    fontFamily = ysDisplayMedium,
                    color = colorResource(id = R.color.yn_color),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp)
                )

                Text(
                    text = track.artistName,
                    fontSize = 14.sp,
                    fontFamily = ysDisplayMedium,
                    color = colorResource(id = R.color.yn_color),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(51.dp)
                            .clickable { showBottomSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_playlist),
                            contentDescription = stringResource(id = R.string.add_to_playlist),
                            tint = colorResource(id = R.color.yn_color),
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(RoundedCornerShape(44.dp))
                            .clickable { viewModel.toggle() },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(
                                id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                            ),
                            contentDescription = stringResource(id = R.string.player),
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(51.dp)
                            .clickable { viewModel.onFavoriteClicked() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isFavorite) R.drawable.ic_like_filled else R.drawable.ic_favorite
                            ),
                            contentDescription = stringResource(id = R.string.favorites),
                            tint = androidx.compose.ui.graphics.Color.Unspecified,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Text(
                    text = currentTime,
                    fontSize = 14.sp,
                    fontFamily = ysDisplayBold,
                    color = colorResource(id = R.color.yn_color),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 30.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TrackInfoRow(label = stringResource(id = R.string.duration), value = track.getFormattedTime())
                    TrackInfoRow(label = stringResource(id = R.string.album), value = track.collectionName ?: stringResource(id = R.string.unknown_album))
                    TrackInfoRow(label = stringResource(id = R.string.year), value = track.getReleaseYear())
                    TrackInfoRow(label = stringResource(id = R.string.genre), value = track.primaryGenreName ?: stringResource(id = R.string.unknown_genre))
                    TrackInfoRow(label = stringResource(id = R.string.country), value = track.country ?: stringResource(id = R.string.unknown_country))
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    containerColor = colorResource(id = R.color.background)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.66f)
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.add_to_playlist),
                            fontSize = 19.sp,
                            fontFamily = ysDisplayMedium,
                            color = colorResource(id = R.color.yn_color),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 16.dp)
                        )

                        Button(
                            onClick = {
                                showBottomSheet = false
                                onNewPlaylistClick()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(id = R.color.text_primary),
                                contentColor = colorResource(id = R.color.textColor)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.new_pl),
                                fontFamily = ysDisplayMedium,
                                fontSize = 14.sp
                            )
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(playlists) { playlist ->
                                PlaylistBottomSheetItem(
                                    playlist = playlist,
                                    onClick = { viewModel.onPlaylistClicked(playlist) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = ysDisplayMedium,
            color = colorResource(id = R.color.albumTextView),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontFamily = ysDisplayMedium,
            color = colorResource(id = R.color.yn_color),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun PlaylistBottomSheetItem(playlist: Playlist, onClick: () -> Unit) {
    val context = LocalContext.current
    val trackCountText = context.resources.getQuantityString(
        R.plurals.tracks_count,
        playlist.tracksCount,
        playlist.tracksCount
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = if (playlist.coverPath.isNotBlank()) File(playlist.coverPath) else null,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.ic_playlist_placeholder),
            error = painterResource(id = R.drawable.ic_playlist_placeholder),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(2.dp))
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = playlist.name,
                fontSize = 16.sp,
                color = colorResource(id = R.color.yn_color),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = trackCountText,
                fontSize = 12.sp,
                color = colorResource(id = R.color.albumTextView),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}