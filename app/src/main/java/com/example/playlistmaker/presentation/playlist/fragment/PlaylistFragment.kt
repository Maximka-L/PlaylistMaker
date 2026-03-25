package com.example.playlistmaker.presentation.playlist.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.playlist.viewmodel.PlaylistEvent
import com.example.playlistmaker.presentation.playlist.viewmodel.PlaylistViewModel
import com.example.playlistmaker.presentation.search.adapter.TracksAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import androidx.core.os.bundleOf

class PlaylistFragment : Fragment(R.layout.fragment_playlist_info) {

    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistViewModel by viewModel()

    private lateinit var tracksAdapter: TracksAdapter

    private var playlistId: Long = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlaylistInfoBinding.bind(view)

        playlistId = arguments?.getLong("playlistId") ?: 0L

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.shareButton.setOnClickListener {
            viewModel.onShareClicked(getTracksCountText())
        }

        initMenu()
        initRecyclerView()
        observeViewModel()
        viewModel.loadPlaylist(playlistId)
    }

    private fun initMenu() {
        binding.moreButton.setOnClickListener {
            binding.overlay.visibility = View.VISIBLE
            binding.menuBottomSheet.visibility = View.VISIBLE
            binding.tracksBottomSheet.visibility = View.GONE
        }

        binding.overlay.setOnClickListener {
            hideMenu()
        }

        binding.menuShare.setOnClickListener {
            hideMenu()
            viewModel.onShareClicked(getTracksCountText())
        }

        binding.menuDelete.setOnClickListener {
            hideMenu()
            showDeletePlaylistDialog()
        }

        binding.menuEdit.setOnClickListener {
            hideMenu()
            val id = viewModel.playlist.value?.id ?: return@setOnClickListener
            findNavController().navigate(
                R.id.action_playlistFragment_to_editPlaylistFragment,
                bundleOf("playlistId" to id)
            )
        }
    }

    private fun hideMenu() {
        binding.overlay.visibility = View.GONE
        binding.menuBottomSheet.visibility = View.GONE
        binding.tracksBottomSheet.visibility = View.VISIBLE
    }

    private fun getTracksCountText(): String {
        val count = viewModel.tracks.value?.size ?: 0
        return resources.getQuantityString(R.plurals.tracks_count, count, count)
    }

    private fun initRecyclerView() {
        tracksAdapter = TracksAdapter(
            emptyList(),
            onItemClick = { track ->
                val action =
                    PlaylistFragmentDirections.actionPlaylistFragmentToAudioPlayerFragment(track)
                findNavController().navigate(action)
            },
            onItemLongClick = { track ->
                showRemoveTrackDialog(track)
            }
        )

        binding.tracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksRecyclerView.adapter = tracksAdapter
    }

    private fun showRemoveTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.WhiteDialog)
            .setMessage(R.string.remove_track_message)
            .setNegativeButton(R.string.dialog_no) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.dialog_yes) { dialog, _ ->
                viewModel.removeTrack(track)
                dialog.dismiss()
            }
            .show()
    }

    private fun showDeletePlaylistDialog() {
        val name = viewModel.playlist.value?.name ?: ""
        MaterialAlertDialogBuilder(requireContext(), R.style.WhiteDialog)
            .setMessage(getString(R.string.delete_playlist_message, name))
            .setNegativeButton(R.string.delete_playlist_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.delete_playlist_confirm) { dialog, _ ->
                viewModel.deletePlaylist()
                dialog.dismiss()
            }
            .show()
    }

    private fun observeViewModel() {
        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            binding.playlistTitle.text = playlist.name
            binding.playlistDescription.text = playlist.description
            binding.playlistDescription.visibility =
                if (playlist.description.isBlank()) View.GONE else View.VISIBLE

            binding.menuPlaylistTitle.text = playlist.name

            val trackCountText = resources.getQuantityString(
                R.plurals.tracks_count,
                playlist.trackIds.size,
                playlist.trackIds.size
            )

            binding.menuPlaylistCount.text = trackCountText

            val durationText = viewModel.duration.value ?: "0"
            binding.playlistInfo.text = getString(
                R.string.playlist_info,
                durationText,
                trackCountText
            )

            if (playlist.coverPath.isNotBlank()) {
                Glide.with(this@PlaylistFragment)
                    .load(File(playlist.coverPath))
                    .placeholder(R.drawable.ic_placeholder1)
                    .error(R.drawable.ic_placeholder1)
                    .fallback(R.drawable.ic_placeholder1)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .centerCrop()
                    .into(binding.playlistCover)

                Glide.with(this@PlaylistFragment)
                    .load(File(playlist.coverPath))
                    .placeholder(R.drawable.ic_placeholder1)
                    .error(R.drawable.ic_placeholder1)
                    .fallback(R.drawable.ic_placeholder1)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .centerCrop()
                    .into(binding.menuPlaylistCover)
            } else {
                binding.playlistCover.setImageResource(R.drawable.ic_placeholder1)
                binding.menuPlaylistCover.setImageResource(R.drawable.ic_placeholder1)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collect { event ->
                when (event) {
                    PlaylistEvent.ShowEmptyShareMessage -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.empty_playlist_share_message),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is PlaylistEvent.SharePlaylist -> {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, event.text)
                        }
                        startActivity(
                            Intent.createChooser(
                                intent,
                                getString(R.string.share_playlist)
                            )
                        )
                    }

                    PlaylistEvent.NavigateBack -> {
                        findNavController().navigateUp()
                    }
                }
            }
        }

        viewModel.duration.observe(viewLifecycleOwner) { duration ->
            val playlist = viewModel.playlist.value
            val trackCount = playlist?.trackIds?.size ?: 0

            val trackCountText = resources.getQuantityString(
                R.plurals.tracks_count,
                trackCount,
                trackCount
            )

            binding.playlistInfo.text = getString(
                R.string.playlist_info,
                duration,
                trackCountText
            )
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            tracksAdapter.updateDataset(tracks)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}