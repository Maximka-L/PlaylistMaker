package com.example.playlistmaker.presentation.media.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.presentation.media.adapter.PagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MediaFragment : Fragment(R.layout.fragment_media) {

    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMediaBinding.bind(view)

        setupViewPager()
        observeCreatedPlaylistMessage()
    }

    private fun setupViewPager() {
        binding.pager.adapter = PagerAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tracks)
                else -> getString(R.string.playlists)
            }
        }.attach()
    }

    private fun observeCreatedPlaylistMessage() {
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("playlist_created_message")
            ?.observe(viewLifecycleOwner) { message ->
                showCustomToast(message)
                findNavController().currentBackStackEntry
                    ?.savedStateHandle
                    ?.remove<String>("playlist_created_message")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}