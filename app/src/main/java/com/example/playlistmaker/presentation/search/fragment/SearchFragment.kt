package com.example.playlistmaker.presentation.search.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.search.SearchScreenState
import com.example.playlistmaker.presentation.search.viewmodel.SearchViewModel
import com.example.playlistmaker.presentation.search.adapter.TracksAdapter
import com.google.android.material.button.MaterialButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var infoBlock: LinearLayout
    private lateinit var emptyIcon: ImageView
    private lateinit var emptyText: TextView
    private lateinit var emptyButton: MaterialButton
    private lateinit var youSearchedText: TextView
    private lateinit var clearHistoryButton: MaterialButton

    private lateinit var historyRecycler: RecyclerView
    private lateinit var searchResultsRecycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var historyBlock: LinearLayout

    private lateinit var historyAdapter: TracksAdapter
    private lateinit var searchResultsAdapter: TracksAdapter

    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        initRecycler()
        initListeners()
        observeViewModel()

        viewModel.onScreenOpened(isNetworkAvailable())
    }

    private fun initViews(view: View) {
        searchEditText = view.findViewById(R.id.searchEditText)
        clearButton = view.findViewById(R.id.clearButton)
        infoBlock = view.findViewById(R.id.infoblock)
        emptyIcon = view.findViewById(R.id.empty_icon)
        emptyText = view.findViewById(R.id.empty_text)
        emptyButton = view.findViewById(R.id.empty_button)
        youSearchedText = view.findViewById(R.id.youserchs)
        clearHistoryButton = view.findViewById(R.id.clear_histors)

        historyBlock = view.findViewById(R.id.historyBlock)
        historyRecycler = view.findViewById(R.id.recycler)
        searchResultsRecycler = view.findViewById(R.id.searchResultsRecycler)

        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun initRecycler() {
        historyAdapter = TracksAdapter(emptyList()) { track ->
            viewModel.onTrackClicked(track)
        }
        historyRecycler.adapter = historyAdapter

        searchResultsAdapter = TracksAdapter(emptyList()) { track ->
            viewModel.onTrackClicked(track)
        }
        searchResultsRecycler.adapter = searchResultsAdapter
    }


    private fun initListeners() {
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            searchEditText.clearFocus()
            hideKeyboard()
            viewModel.onClearSearchClicked()
        }

        clearHistoryButton.setOnClickListener {
            viewModel.onClearHistoryClicked()
        }

        emptyButton.setOnClickListener {
            viewModel.onSearchButtonClicked()
        }

        searchEditText.doOnTextChanged { text, _, _, _ ->
            val query = text?.toString().orEmpty()
            clearButton.isVisible = query.isNotEmpty()
            viewModel.onSearchTextChanged(query)
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onSearchButtonClicked()
                true
            } else {
                false
            }
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        viewModel.openTrackEvent.observe(viewLifecycleOwner) { track ->
            openPlayer(track)
        }
    }

    private fun renderState(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Loading -> showLoading()
            is SearchScreenState.History -> showHistory(state.tracks)
            is SearchScreenState.Content -> showContent(state.tracks)
            is SearchScreenState.Empty -> showEmpty(state.isInternetError)
        }
    }

    private fun showLoading() {
        progressBar.isVisible = true
        historyBlock.isVisible = false
        searchResultsRecycler.isVisible = false
        infoBlock.isVisible = false
        youSearchedText.isVisible = false
        clearHistoryButton.isVisible = false
    }

    private fun showHistory(tracks: List<Track>) {
        historyAdapter.updateDataset(tracks)

        val hasHistory = tracks.isNotEmpty()
        historyBlock.isVisible = hasHistory
        historyRecycler.isVisible = hasHistory
        youSearchedText.isVisible = hasHistory
        clearHistoryButton.isVisible = hasHistory

        searchResultsRecycler.isVisible = false
        infoBlock.isVisible = false
        progressBar.isVisible = false
    }

    private fun showContent(tracks: List<Track>) {
        searchResultsAdapter.updateDataset(tracks)

        searchResultsRecycler.isVisible = true
        historyBlock.isVisible = false
        infoBlock.isVisible = false
        progressBar.isVisible = false
        youSearchedText.isVisible = false
        clearHistoryButton.isVisible = false
    }

    private fun showEmpty(isInternetError: Boolean) {
        searchResultsAdapter.updateDataset(emptyList())

        progressBar.isVisible = false
        historyBlock.isVisible = false
        searchResultsRecycler.isVisible = false
        youSearchedText.isVisible = false
        clearHistoryButton.isVisible = false

        infoBlock.isVisible = true
        emptyIcon.isVisible = true
        emptyText.isVisible = true

        if (isInternetError) {
            emptyIcon.setImageResource(R.drawable.ic_not_int)
            emptyText.text =
                "Проблемы со связью\nЗагрузка не удалась. Проверьте подключение к интернету"
            emptyButton.isVisible = true
        } else {
            emptyIcon.setImageResource(R.drawable.ic_light_mode)
            emptyText.text = "Ничего не нашлось"
            emptyButton.isVisible = false
        }
    }

    private fun openPlayer(track: Track) {
        val action =
            SearchFragmentDirections
                .actionSearchFragmentToAudioPlayerFragment(track)

        findNavController().navigate(
            SearchFragmentDirections
                .actionSearchFragmentToAudioPlayerFragment(track)
        )
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun isNetworkAvailable(): Boolean {
        val cm =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}