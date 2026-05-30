package com.example.playlistmaker.presentation.media.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.search.ui.TrackItem

private val ysDisplayMedium = FontFamily(Font(R.font.ys_display_medium, FontWeight.Medium))

@Composable
fun TrackScreen(
    state: com.example.playlistmaker.presentation.media.favorites.FavoritesState,
    onTrackClick: (Track) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background))
    ) {
        when (state) {
            is com.example.playlistmaker.presentation.media.favorites.FavoritesState.Empty -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(106.dp))

                    Image(
                        painter = painterResource(id = R.drawable.ic_light_mode),
                        contentDescription = null,
                        modifier = Modifier.wrapContentSize()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(id = R.string.string_emty),
                        fontSize = 19.sp,
                        fontFamily = ysDisplayMedium,
                        color = colorResource(id = R.color.text_primary),
                        textAlign = TextAlign.Center
                    )
                }
            }
            is com.example.playlistmaker.presentation.media.favorites.FavoritesState.Content -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(state.tracks) { track ->
                        TrackItem(
                            track = track,
                            onClick = { onTrackClick(track) }
                        )
                    }
                }
            }
        }
    }
}