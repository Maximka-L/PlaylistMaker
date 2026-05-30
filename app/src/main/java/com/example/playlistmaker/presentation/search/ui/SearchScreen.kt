package com.example.playlistmaker.presentation.search.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.search.SearchScreenState
import com.example.playlistmaker.presentation.search.viewmodel.SearchViewModel

private val ysDisplayMedium = FontFamily(Font(R.font.ys_display_medium, FontWeight.Medium))
private val ysDisplayRegular = FontFamily(Font(R.font.ys_display_regular, FontWeight.Normal))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit
) {
    val searchText by viewModel.searchText.observeAsState("")
    val state by viewModel.state.observeAsState(SearchScreenState.Loading)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background))
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.search),
                    fontSize = 22.sp,
                    fontFamily = ysDisplayMedium,
                    color = colorResource(id = R.color.text_primary)
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(id = R.color.background)
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(36.dp)
                .background(
                    color = colorResource(id = R.color.front),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = null,
                tint = colorResource(id = R.color.search),
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = searchText,
                onValueChange = { viewModel.onSearchTextChanged(it) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                cursorBrush = SolidColor(colorResource(id = R.color.cursor_color)),
                textStyle = TextStyle(
                    color = colorResource(id = R.color.search_text),
                    fontSize = 16.sp,
                    fontFamily = ysDisplayRegular
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { viewModel.onSearchButtonClicked() }),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (searchText.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.search),
                                color = colorResource(id = R.color.search),
                                fontSize = 16.sp,
                                fontFamily = ysDisplayRegular
                            )
                        }
                        innerTextField()
                    }
                }
            )

            if (searchText.isNotEmpty()) {
                IconButton(
                    onClick = { viewModel.onSearchTextChanged("") },
                    modifier = Modifier.size(18.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_clear_24),
                        contentDescription = null,
                        tint = colorResource(id = R.color.clear),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (val currentState = state) {
                is SearchScreenState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 140.dp)
                            .size(width = 45.dp, height = 44.dp),
                        color = colorResource(id = R.color.primary)
                    )
                }

                is SearchScreenState.Content -> {
                    TracksList(
                        tracks = currentState.tracks,
                        onTrackClick = onTrackClick
                    )
                }

                is SearchScreenState.History -> {
                    if (currentState.tracks.isNotEmpty()) {
                        SearchHistorySection(
                            tracks = currentState.tracks,
                            onTrackClick = onTrackClick,
                            onClearHistoryClick = { viewModel.onClearHistoryClicked() }
                        )
                    }
                }

                is SearchScreenState.Empty -> {
                    EmptySearchPlaceholder(
                        isInternetError = currentState.isInternetError,
                        onRefreshClick = { viewModel.onSearchButtonClicked() }
                    )
                }
            }
        }
    }
}

@Composable
private fun TracksList(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(tracks) { track ->
            TrackItem(
                track = track,
                onClick = { onTrackClick(track) }
            )
        }
    }
}

@Composable
private fun SearchHistorySection(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onClearHistoryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.history_title),
            fontSize = 16.sp,
            fontFamily = ysDisplayMedium,
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.text_primary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(tracks) { track ->
                TrackItem(
                    track = track,
                    onClick = { onTrackClick(track) }
                )
            }
        }

        Button(
            onClick = onClearHistoryClick,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.text_primary),
                contentColor = colorResource(id = R.color.textColor)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = stringResource(id = R.string.clear_history),
                fontSize = 14.sp,
                fontFamily = ysDisplayMedium,
                color = colorResource(id = R.color.textColor)
            )
        }
    }
}

@Composable
private fun EmptySearchPlaceholder(
    isInternetError: Boolean,
    onRefreshClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(106.dp))

        val imageRes = if (isInternetError) R.drawable.ic_not_int else R.drawable.ic_not_int
        val messageText = if (isInternetError) {
            stringResource(id = R.string.error_no_connection)
        } else {
            stringResource(id = R.string.nothing_was_found)
        }

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = messageText,
            fontSize = 19.sp,
            fontFamily = ysDisplayMedium,
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.text_primary),
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        if (isInternetError) {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRefreshClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.text_primary),
                    contentColor = colorResource(id = R.color.textColor)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.update),
                    fontFamily = ysDisplayMedium,
                    color = colorResource(id = R.color.textColor)
                )
            }
        }
    }
}

@Composable
fun TrackItem(
    track: Track,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(61.dp)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.getCoverArtwork(),
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.ic_app),
            error = painterResource(id = R.drawable.ic_app),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(start = 13.dp)
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp))
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, end = 8.dp)
        ) {
            Text(
                text = track.trackName,
                fontSize = 16.sp,
                fontFamily = ysDisplayRegular,
                color = colorResource(id = R.color.text_primary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = track.artistName,
                    fontSize = 11.sp,
                    fontFamily = ysDisplayRegular,
                    color = colorResource(id = R.color.albumText),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_dots),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(13.dp)
                )
                Text(
                    text = track.getFormattedTime(),
                    fontSize = 11.sp,
                    fontFamily = ysDisplayRegular,
                    color = colorResource(id = R.color.albumText)
                )
            }
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_appe),
            contentDescription = null,
            tint = colorResource(id = R.color.albumText),
            modifier = Modifier
                .padding(end = 12.dp)
                .size(16.dp)
        )
    }
}