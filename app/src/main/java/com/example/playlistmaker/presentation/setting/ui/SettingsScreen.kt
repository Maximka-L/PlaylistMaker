package com.example.playlistmaker.presentation.setting.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.setting.viewmodel.SettingsViewModel

private val ysDisplayMedium = FontFamily(Font(R.font.ys_display_medium, FontWeight.Medium))
private val ysDisplayRegular = FontFamily(Font(R.font.ys_display_regular, FontWeight.Normal))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val darkThemeEnabled by viewModel.darkTheme.observeAsState(false)
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background))
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.settings_title),
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
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.dark_theme),
                fontSize = 16.sp,
                fontFamily = ysDisplayRegular,
                color = colorResource(id = R.color.text_primary)
            )

            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(26.dp)
                    .background(
                        color = colorResource(
                            id = if (darkThemeEnabled) R.color.switch_track_active_color
                            else R.color.switch_track_inactive_color
                        ),
                        shape = RoundedCornerShape(13.dp)
                    )
                    .clickable { viewModel.changeTheme(!darkThemeEnabled) }
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(
                            color = colorResource(
                                id = if (darkThemeEnabled) R.color.switch_thumb_active_color
                                else R.color.switch_thumb_inactive_color
                            ),
                            shape = RoundedCornerShape(11.dp)
                        )
                        .align(if (darkThemeEnabled) Alignment.CenterEnd else Alignment.CenterStart)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.course_link))
                    }
                    context.startActivity(Intent.createChooser(intent, null))
                }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.share_app),
                fontSize = 16.sp,
                fontFamily = ysDisplayRegular,
                color = colorResource(id = R.color.text_primary)
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = null,
                tint = colorResource(id = R.color.color_icon_setting),
                modifier = Modifier.size(24.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.support_email)))
                        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_subject))
                        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_body))
                    }
                    context.startActivity(intent)
                }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.contact_support),
                fontSize = 16.sp,
                fontFamily = ysDisplayRegular,
                color = colorResource(id = R.color.text_primary)
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_suport),
                contentDescription = null,
                tint = colorResource(id = R.color.color_icon_setting),
                modifier = Modifier.size(24.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(context.getString(R.string.agreement_link))
                        )
                    )
                }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.user_agreement),
                fontSize = 16.sp,
                fontFamily = ysDisplayRegular,
                color = colorResource(id = R.color.text_primary)
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_appe),
                contentDescription = null,
                tint = colorResource(id = R.color.color_icon_setting),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}