package com.example.playlistmaker.presentation.media

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.playlistmaker.databinding.FragmentTrackBinding
import com.example.playlistmaker.presentation.media.view_model.TrackFragmentViewModel
import kotlin.getValue


class TrackFragment : Fragment() {
    private var _binding: FragmentTrackBinding? = null
    private val viewModel: TrackFragmentViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    { _binding = FragmentTrackBinding.inflate(inflater , container , false)
        return binding.root

    }



    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TrackFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}