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

open class CreatePlaylistFragment : Fragment(R.layout.fragment_create_playlist) {

    protected var _binding: FragmentCreatePlaylistBinding? = null
    protected val binding get() = _binding!!

    protected open val viewModel: CreatePlaylistViewModel by viewModel()

    private var isImagePickerOpening = false
    protected var isPlaylistCreated = false

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
            handleSaveAction()
        }
    }

    protected open fun handleBackAction() {
        if (hasUnsavedChanges()) {
            showExitConfirmationDialog()
        } else {
            findNavController().popBackStack()
        }
    }

    protected open fun handleSaveAction() {
        val name = binding.playlistNameEditText.text.toString().trim()
        val description = binding.playlistDescriptionEditText.text.toString().trim()

        if (name.isBlank()) return

        val coverPath = viewModel.coverUri.value?.let { uriString ->
            saveImageToPrivateStorage(Uri.parse(uriString))
        } ?: ""

        viewModel.createPlaylist(name = name, description = description, coverPath = coverPath)
        isPlaylistCreated = true

        findNavController().previousBackStackEntry
            ?.savedStateHandle
            ?.set("playlist_created_message", getString(R.string.playlist_created, name))

        findNavController().popBackStack()
    }

    protected fun hasUnsavedChanges(): Boolean {
        if (isPlaylistCreated) return false

        return binding.playlistNameEditText.text?.isNotBlank() == true ||
                binding.playlistDescriptionEditText.text?.isNotBlank() == true ||
                !viewModel.coverUri.value.isNullOrBlank()
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.finish_playlist_creation_title))
            .setMessage(getString(R.string.finish_playlist_creation_message))
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.finish)) { _, _ ->
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

    protected fun saveImageToPrivateStorage(uri: Uri): String {
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