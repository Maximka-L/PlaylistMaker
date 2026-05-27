package com.example.playlistmaker.presentation.playlist.fragment

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.playlist.viewmodel.CreatePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

private val ysDisplayMedium = FontFamily(Font(R.font.ys_display_medium, FontWeight.Medium))
private val ysDisplayRegular = FontFamily(Font(R.font.ys_display_regular, FontWeight.Normal))

open class CreatePlaylistFragment : Fragment() {

    open val viewModel: CreatePlaylistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    val coverUriState by viewModel.coverUri.observeAsState(null)
                    val coverUri = coverUriState ?: ""

                    PlaylistFormScreen(
                        screenTitle = stringResource(id = R.string.new_playlist),
                        buttonText = stringResource(id = R.string.create),
                        initialName = "",
                        initialDescription = "",
                        initialCoverUri = coverUri,
                        onBack = { onBackClicked(coverUri) },
                        onCoverUriChanged = { viewModel.setCoverUri(it) },
                        onSave = { name, description, finalUri ->
                            val finalCoverPath = if (finalUri.isNotBlank()) {
                                saveImageToPrivateStorage(Uri.parse(finalUri))
                            } else ""
                            viewModel.createPlaylist(name, description, finalCoverPath)

                            findNavController().previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("playlist_created_message", getString(R.string.playlist_created, name))
                            findNavController().popBackStack()
                        }
                    )
                }
            }
        }
    }

    protected open fun onBackClicked(coverUri: String) {
        if (coverUri.isNotBlank()) {
            showExitConfirmationDialog()
        } else {
            findNavController().popBackStack()
        }
    }

    protected fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.finish_playlist_creation_title))
            .setMessage(getString(R.string.finish_playlist_creation_message))
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.finish)) { _, _ ->
                findNavController().popBackStack()
            }
            .show()
    }

    protected fun saveImageToPrivateStorage(uri: Uri): String {
        val picturesDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val filePath = File(picturesDir, "playlist_covers")
        if (!filePath.exists()) filePath.mkdirs()

        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")
        requireContext().contentResolver.openInputStream(uri).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
        }
        return file.absolutePath
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistFormScreen(
    screenTitle: String,
    buttonText: String,
    initialName: String,
    initialDescription: String,
    initialCoverUri: String,
    onBack: (hasChanges: Boolean) -> Unit,
    onCoverUriChanged: (String) -> Unit,
    onSave: (name: String, description: String, coverUri: String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var description by remember { mutableStateOf(initialDescription) }

    LaunchedEffect(initialName) { name = initialName }
    LaunchedEffect(initialDescription) { description = initialDescription }

    val hasChanges = name.isNotBlank() || description.isNotBlank() || initialCoverUri.isNotBlank()

    BackHandler { onBack(hasChanges) }

    val pickMedia = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) onCoverUriChanged(uri.toString())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = screenTitle, fontFamily = ysDisplayMedium, fontSize = 22.sp, color = colorResource(id = R.color.text_primary)) },
                navigationIcon = {
                    IconButton(onClick = { onBack(hasChanges) }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Назад", tint = colorResource(id = R.color.icon_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.background))
            )
        },
        containerColor = colorResource(id = R.color.background)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            val dashColor = colorResource(id = R.color.color_icon_setting)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 26.dp, end = 24.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                contentAlignment = Alignment.Center
            ) {
                if (initialCoverUri.isNotBlank()) {
                    AsyncImage(
                        model = initialCoverUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRoundRect(
                            color = dashColor,
                            style = Stroke(
                                width = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(30.dp.toPx(), 30.dp.toPx()), 0f)
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx())
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_addd_playlist),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 29.dp, end = 24.dp)
            ) {
                val nameInteractionSource = remember { MutableInteractionSource() }
                val isNameFocused by nameInteractionSource.collectIsFocusedAsState()
                val nameBorderColor = if (isNameFocused || name.isNotBlank()) {
                    colorResource(id = R.color.create_playlist_active)
                } else {
                    colorResource(id = R.color.not_active)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(1.dp, nameBorderColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (name.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.name_media),
                            color = colorResource(id = R.color.albumText),
                            fontSize = 16.sp,
                            fontFamily = ysDisplayRegular
                        )
                    }
                    BasicTextField(
                        value = name,
                        onValueChange = { name = it },
                        singleLine = true,
                        maxLines = 1,
                        interactionSource = nameInteractionSource,
                        textStyle = TextStyle(
                            color = colorResource(id = R.color.albumText),
                            fontSize = 16.sp,
                            fontFamily = ysDisplayRegular
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val descInteractionSource = remember { MutableInteractionSource() }
                val isDescFocused by descInteractionSource.collectIsFocusedAsState()
                val descBorderColor = if (isDescFocused || description.isNotBlank()) {
                    colorResource(id = R.color.create_playlist_active)
                } else {
                    colorResource(id = R.color.not_active)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 56.dp)
                        .border(1.dp, descBorderColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (description.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.description),
                            color = colorResource(id = R.color.albumText),
                            fontSize = 16.sp,
                            fontFamily = ysDisplayRegular
                        )
                    }
                    BasicTextField(
                        value = description,
                        onValueChange = { description = it },
                        interactionSource = descInteractionSource,
                        textStyle = TextStyle(
                            color = colorResource(id = R.color.albumText),
                            fontSize = 16.sp,
                            fontFamily = ysDisplayRegular
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 17.dp, end = 17.dp, bottom = 24.dp)
            ) {
                Button(
                    onClick = { onSave(name.trim(), description.trim(), initialCoverUri) },
                    enabled = name.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.create_playlist_active),
                        disabledContainerColor = colorResource(id = R.color.not_active),
                        contentColor = colorResource(id = R.color.white),
                        disabledContentColor = colorResource(id = R.color.white)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text(
                        text = buttonText,
                        fontFamily = ysDisplayMedium,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}