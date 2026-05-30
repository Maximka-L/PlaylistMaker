package com.example.playlistmaker.presentation.search.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

private val ysDisplayMedium = FontFamily(Font(R.font.ys_display_medium, FontWeight.Medium))
private val ysDisplayRegular = FontFamily(Font(R.font.ys_display_regular, FontWeight.Normal))

@Composable
fun Trackitem(
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
            model = track.artworkUrl100,
            contentDescription = null,
            contentScale = ContentScale.Crop,

            placeholder = painterResource(id = R.drawable.ic_playlist_placeholder),
            error = painterResource(id = R.drawable.ic_playlist_placeholder),
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
                    color = colorResource(id = R.color.albumTextView),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_dots),
                    contentDescription = null,
                    tint = colorResource(id = R.color.albumTextView),
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(13.dp)
                )

                Text(
                    text = track.trackTime,
                    fontSize = 11.sp,
                    fontFamily = ysDisplayRegular,
                    color = colorResource(id = R.color.albumTextView),
                    maxLines = 1
                )
            }
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_appe),
            contentDescription = null,
            tint = colorResource(id = R.color.color_icon_setting),
            modifier = Modifier
                .padding(end = 12.dp)
                .size(24.dp)
        )
    }
}