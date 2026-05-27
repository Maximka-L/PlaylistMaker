package com.example.playlistmaker.presentation.media.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
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
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist
import java.io.File

private val ysDisplayMedium = FontFamily(Font(R.font.ys_display_medium, FontWeight.Medium))
private val ysDisplayRegular = FontFamily(Font(R.font.ys_display_regular, FontWeight.Normal))

@Composable
fun PlayListScreen(
    playlists: List<Playlist>,
    onCreatePlaylistClick: () -> Unit,
    onPlaylistClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onCreatePlaylistClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.text_primary),
                contentColor = colorResource(id = R.color.textColor)
            ),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.new_pl),
                fontFamily = ysDisplayMedium,
                fontSize = 14.sp
            )
        }

        if (playlists.isEmpty()) {
            Spacer(modifier = Modifier.height(106.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_light_mode),
                contentDescription = null,
                modifier = Modifier.wrapContentSize()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.track_list_play),
                fontSize = 19.sp,
                fontFamily = ysDisplayMedium,
                color = colorResource(id = R.color.text_primary),
                textAlign = TextAlign.Center
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(playlists) { playlist ->
                    PlaylistItemView(
                        playlist = playlist,
                        onClick = { onPlaylistClick(playlist.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylistItemView(playlist: Playlist, onClick: () -> Unit) {
    val context = LocalContext.current
    val trackCountText = context.resources.getQuantityString(
        R.plurals.tracks_count,
        playlist.tracksCount,
        playlist.tracksCount
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = if (playlist.coverPath.isNotBlank()) Uri.fromFile(File(playlist.coverPath)) else R.drawable.ic_playlist_placeholder,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(colorResource(id = R.color.front))
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = playlist.name,
            fontSize = 12.sp,
            fontFamily = ysDisplayRegular,
            color = colorResource(id = R.color.text_primary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = trackCountText,
            fontSize = 11.sp,
            fontFamily = ysDisplayRegular,
            color = colorResource(id = R.color.albumTextView),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}