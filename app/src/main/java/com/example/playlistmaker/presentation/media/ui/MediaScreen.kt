package com.example.playlistmaker.presentation.media.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.media.favorites.FavoritesState
import com.example.playlistmaker.presentation.media.favorites.FavoritesViewModel
import com.example.playlistmaker.presentation.media.view_model.PlayListFragmentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val ysDisplayMedium = FontFamily(Font(R.font.ys_display_medium, FontWeight.Medium))

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MediaScreen(
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    favoritesViewModel: FavoritesViewModel,
    playListsViewModel: PlayListFragmentViewModel,
    onTrackClick: (Track) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    onPlaylistClick: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.media_library),
                        fontSize = 22.sp,
                        fontFamily = ysDisplayMedium,
                        color = colorResource(id = R.color.text_primary)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.background)
                )
            )

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = colorResource(id = R.color.background),
                contentColor = colorResource(id = R.color.text_primary),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = colorResource(id = R.color.text_primary)
                    )
                }
            ) {
                val titles = listOf(R.string.tracks, R.string.playlists)
                titles.forEachIndexed { index, titleRes ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = stringResource(id = titleRes),
                                fontSize = 14.sp,
                                fontFamily = ysDisplayMedium
                            )
                        },
                        selectedContentColor = colorResource(id = R.color.text_primary),
                        unselectedContentColor = colorResource(id = R.color.text_primary)
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                if (page == 0) {
                    val favoriteState by favoritesViewModel.state.observeAsState(FavoritesState.Empty)
                    TrackScreen(
                        state = favoriteState,
                        onTrackClick = onTrackClick
                    )
                } else {
                    val playlists by playListsViewModel.playlists.observeAsState(emptyList())
                    PlayListScreen(
                        playlists = playlists,
                        onCreatePlaylistClick = onCreatePlaylistClick,
                        onPlaylistClick = onPlaylistClick
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = colorResource(id = R.color.text_primary),
                contentColor = colorResource(id = R.color.textColor)
            )
        }
    }
}