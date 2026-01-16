package com.example.playlistmaker.presentation.media

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.example.playlistmaker.databinding.FragmentPlayListBinding
import com.example.playlistmaker.presentation.media.view_model.PlayListFragmentViewModel
import kotlin.getValue


class PlayListFragment : Fragment() {

    private var _binding: FragmentPlayListBinding? = null
    private val viewModel: PlayListFragmentViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayListBinding.inflate(inflater , container , false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlayListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}