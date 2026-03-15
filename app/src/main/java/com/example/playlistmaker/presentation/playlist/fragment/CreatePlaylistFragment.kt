package com.example.playlistmaker.presentation.playlist.fragment

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.presentation.playlist.viewmodel.CreatePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistFragment : Fragment(R.layout.fragment_create_playlist) {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreatePlaylistViewModel by viewModel()

    private var isImagePickerOpening = false
    private var isPlaylistCreated = false

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            isImagePickerOpening = false
            if (uri != null) {
                viewModel.setCoverUri(uri.toString())
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentCreatePlaylistBinding.bind(view)

        updateCreateButtonState(false)

        binding.playlistNameEditText.doAfterTextChanged { text ->
            updateCreateButtonState(!text.isNullOrBlank())
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackAction()
        }

        binding.toolbar.setNavigationOnClickListener {
            handleBackAction()
        }

        binding.coverBackground.setOnClickListener {
            if (isImagePickerOpening) return@setOnClickListener

            isImagePickerOpening = true
            pickMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        observeViewModel()

        binding.createButton.setOnClickListener {
            val name = binding.playlistNameEditText.text.toString().trim()
            val description = binding.playlistDescriptionEditText.text.toString().trim()

            if (name.isBlank()) return@setOnClickListener

            val coverPath = viewModel.coverUri.value?.let { uriString ->
                saveImageToPrivateStorage(Uri.parse(uriString))
            } ?: ""

            viewModel.createPlaylist(
                name = name,
                description = description,
                coverPath = coverPath
            )

            isPlaylistCreated = true

            findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.set("playlist_created_message", "Плейлист $name создан")

            findNavController().popBackStack()
        }
    }

    private fun handleBackAction() {
        if (hasUnsavedChanges()) {
            showExitConfirmationDialog()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        if (isPlaylistCreated) return false

        return binding.playlistNameEditText.text?.isNotBlank() == true ||
                binding.playlistDescriptionEditText.text?.isNotBlank() == true ||
                !viewModel.coverUri.value.isNullOrBlank()
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Завершить") { _, _ ->
                findNavController().popBackStack()
            }
            .show()
    }

    private fun observeViewModel() {
        viewModel.coverUri.observe(viewLifecycleOwner) { uriString ->
            if (!uriString.isNullOrEmpty()) {
                val uri = Uri.parse(uriString)
                binding.coverBackground.setImageURI(uri)
                binding.coverAddIcon.visibility = View.GONE
            } else {
                binding.coverBackground.setImageResource(R.drawable.bg_add_playlist)
                binding.coverAddIcon.visibility = View.VISIBLE
            }
        }
    }

    private fun updateCreateButtonState(isEnabled: Boolean) {
        binding.createButton.isEnabled = isEnabled
        binding.createButton.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (isEnabled) R.color.create_playlist_active else R.color.not_active
            )
        )
    }

    private fun saveImageToPrivateStorage(uri: Uri): String {
        val picturesDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val filePath = File(picturesDir, "playlist_covers")

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")

        requireContext().contentResolver.openInputStream(uri).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
        }

        return file.absolutePath
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}