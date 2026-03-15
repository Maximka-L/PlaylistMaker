package com.example.playlistmaker.presentation.media.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayListBinding
import com.example.playlistmaker.presentation.media.adapter.PlayListAdapter
import com.example.playlistmaker.presentation.media.view_model.PlayListFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayListFragment : Fragment(R.layout.fragment_play_list) {

    private var _binding: FragmentPlayListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayListFragmentViewModel by viewModel()
    private val adapter = PlayListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlayListBinding.bind(view)

        setupRecycler()
        observeViewModel()

        binding.emptyButton.setOnClickListener {
            requireParentFragment().findNavController()
                .navigate(R.id.createPlaylistFragment)
        }
    }

    private fun setupRecycler() {
        binding.playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            val isEmpty = playlists.isEmpty()

            binding.imageFragm.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.emptyText.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.playlistsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE

            adapter.submitList(playlists)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): PlayListFragment {
            return PlayListFragment()
        }
    }
}