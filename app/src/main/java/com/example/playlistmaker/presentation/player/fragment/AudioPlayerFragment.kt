package com.example.playlistmaker.presentation.player.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.adapter.PlaylistBottomSheetAdapter
import com.example.playlistmaker.presentation.player.service.AudioPlayerService
import com.example.playlistmaker.presentation.player.viewmodel.PlayerViewModel
import com.example.playlistmaker.presentation.player.viewmodel.PlaylistAddStatus
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class AudioPlayerFragment : Fragment(R.layout.fragment_audio_player) {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerViewModel by viewModel()
    private var currentTrack: Track? = null

    private lateinit var playlistAdapter: PlaylistBottomSheetAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private var serviceConnection: ServiceConnection? = null
    private var isBound = false

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAudioPlayerBinding.bind(view)

        currentTrack = arguments?.getParcelable("track")

        if (currentTrack == null) {
            findNavController().popBackStack()
            return
        }

        showTrackInfo(currentTrack!!)
        viewModel.setTrack(currentTrack!!)

        initPlaylistAdapter()
        initBottomSheet()
        setupObservers()
        setupListeners()
        bindPlayerService()
    }

    private fun bindPlayerService() {
        val intent = Intent(requireContext(), AudioPlayerService::class.java).apply {
            putExtra(AudioPlayerService.EXTRA_ARTIST, currentTrack?.artistName ?: "")
            putExtra(AudioPlayerService.EXTRA_TRACK, currentTrack?.trackName ?: "")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as AudioPlayerService.PlayerBinder
                viewModel.onServiceConnected(binder.getService())
                currentTrack?.previewUrl?.let { url ->
                    viewModel.prepare(
                        url = url,
                        artistName = currentTrack?.artistName ?: "",
                        trackName = currentTrack?.trackName ?: ""
                    )
                }
                isBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isBound = false
            }
        }

        requireContext().bindService(
            intent,
            serviceConnection!!,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onStart() {
        super.onStart()
        viewModel.onAppInForeground()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onAppInBackground()
    }

    override fun onDestroyView() {
        viewModel.stopPlayback()
        if (isBound) {
            serviceConnection?.let { requireContext().unbindService(it) }
            isBound = false
        }
        _binding = null
        super.onDestroyView()
    }

    private fun initPlaylistAdapter() {
        playlistAdapter = PlaylistBottomSheetAdapter { playlist ->
            viewModel.onPlaylistClicked(playlist)
        }

        binding.playlistsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playlistAdapter
        }
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
            halfExpandedRatio = 0.6f
        }

        binding.root.post {
            val screenHeight = binding.root.height
            bottomSheetBehavior.peekHeight = (screenHeight * 2 / 3)
            bottomSheetBehavior.maxHeight = (screenHeight * 2 / 3)
        }

        binding.overlay.visibility = View.GONE
        binding.overlay.alpha = 0.6f

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlay.visibility =
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
        })
    }

    private fun setupObservers() {
        viewModel.time.observe(viewLifecycleOwner) {
            binding.currentTimeTextView.text = it
        }

        viewModel.isPlayingLive.observe(viewLifecycleOwner) { playing ->
            binding.playButton.setIsPlaying(playing)
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFav ->
            binding.favoriteButton.setImageResource(
                if (isFav) R.drawable.ic_like_filled else R.drawable.ic_favorite
            )
        }

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.submitList(playlists)
        }

        viewModel.playlistAddStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is PlaylistAddStatus.Added -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    showCustomToast(
                        getString(R.string.added_to_playlist, status.playlistName)
                    )
                    viewModel.clearPlaylistAddStatus()
                }
                is PlaylistAddStatus.AlreadyExists -> {
                    showCustomToast(
                        getString(R.string.track_already_in_playlist, status.playlistName)
                    )
                    viewModel.clearPlaylistAddStatus()
                }
                null -> Unit
            }
        }
    }

    private fun setupListeners() {
        binding.playButton.onPlaybackClick = {
            viewModel.toggle()
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.favoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.addToPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.newPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(R.id.action_audioPlayerFragment_to_createPlaylistFragment)
        }
    }

    private fun showCustomToast(message: String) {
        val layout = layoutInflater.inflate(R.layout.toast_playlist, null)
        val textView = layout.findViewById<TextView>(R.id.toast_text)
        textView.text = message

        val toast = Toast(requireContext())
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 32)
        toast.show()
    }

    private fun showTrackInfo(track: Track) {
        with(binding) {
            trackNameTextView.text = track.trackName
            artistNameTextView.text = track.artistName
            trackDurationTextView.text = track.getFormattedTime()
            albumTextView.text = track.collectionName ?: getString(R.string.unknown_album)
            yearTextView.text = track.getReleaseYear()
            genreTextView.text = track.primaryGenreName ?: getString(R.string.unknown_genre)
            countryTextView.text = track.country ?: getString(R.string.unknown_country)

            Glide.with(this@AudioPlayerFragment)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.ic_placeholder1)
                .error(R.drawable.ic_placeholder1)
                .fallback(R.drawable.ic_placeholder1)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .centerCrop()
                .into(coverImageView)
        }
    }
}