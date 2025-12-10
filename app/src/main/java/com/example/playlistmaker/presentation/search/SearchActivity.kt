package com.example.playlistmaker.presentation.search

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.AudioPlayerActivity
import com.example.playlistmaker.presentation.search.adapter.TracksAdapter
import com.google.android.material.button.MaterialButton

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var toolbar: Toolbar
    private lateinit var infoBlock: LinearLayout
    private lateinit var emptyIcon: ImageView
    private lateinit var emptyText: TextView
    private lateinit var emptyButton: MaterialButton
    private lateinit var youSearchedText: TextView
    private lateinit var clearHistoryButton: MaterialButton
    private lateinit var recycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var historyBlock: LinearLayout

    private lateinit var adapter: TracksAdapter

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.searchRoot)) { view, insets ->
            view.updatePadding(top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top)
            insets
        }

        initViews()
        initRecycler()
        initListeners()
        observeViewModel()


        viewModel.onScreenOpened(isNetworkAvailable())
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)
        infoBlock = findViewById(R.id.infoblock)
        emptyIcon = findViewById(R.id.empty_icon)
        emptyText = findViewById(R.id.empty_text)
        emptyButton = findViewById(R.id.empty_button)
        youSearchedText = findViewById(R.id.youserchs)
        clearHistoryButton = findViewById(R.id.clear_histors)
        recycler = findViewById(R.id.recycler)
        progressBar = findViewById(R.id.progressBar)
        historyBlock = findViewById(R.id.historyBlock)
    }

    private fun initRecycler() {
        adapter = TracksAdapter(emptyList()) { track ->
            viewModel.onTrackClicked(track)
        }
        recycler.adapter = adapter
    }

    private fun initListeners() {
        toolbar.setNavigationOnClickListener { finish() }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            searchEditText.clearFocus()
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(searchEditText.windowToken, 0)
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
        viewModel.state.observe(this) { state ->
            renderState(state)
        }

        viewModel.openTrackEvent.observe(this) { track ->
            openPlayer(track)
        }
    }

    private fun renderState(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Loading -> {
                showLoading()
            }
            is SearchScreenState.History -> {
                showHistory(state.tracks)

            }
            is SearchScreenState.Content -> {
                showContent(state.tracks)
            }
            is SearchScreenState.Empty -> {
                showEmpty(state.isInternetError)
            }
        }
    }

    private fun showLoading() {
        progressBar.isVisible = true

        recycler.isVisible = false
        historyBlock.isVisible = false
        infoBlock.isVisible = false
        youSearchedText.isVisible = false
        clearHistoryButton.isVisible = false
    }

    private fun showHistory(tracks: List<Track>) {
        adapter.updateDataset(tracks)
        adapter.notifyDataSetChanged()
        val hasHistory = tracks.isNotEmpty()
        historyBlock.isVisible = hasHistory
        recycler.isVisible = hasHistory
        youSearchedText.isVisible = hasHistory
        clearHistoryButton.isVisible = hasHistory

        infoBlock.isVisible = false
        progressBar.isVisible = false
    }

    private fun showContent(tracks: List<Track>) {
        adapter.updateDataset(tracks)

        recycler.isVisible = true
        adapter.notifyDataSetChanged()
        historyBlock.isVisible = false
        youSearchedText.isVisible = false
        clearHistoryButton.isVisible = false
        infoBlock.isVisible = false
        progressBar.isVisible = false
    }

    private fun showEmpty(isInternetError: Boolean) {
        adapter.updateDataset(emptyList())
        adapter.notifyDataSetChanged()
        progressBar.isVisible = false
        recycler.isVisible = false
        historyBlock.isVisible = false
        youSearchedText.isVisible = false
        clearHistoryButton.isVisible = false


        infoBlock.isVisible = true
        emptyIcon.isVisible = true
        emptyText.isVisible = true

        if (isInternetError) {
            emptyIcon.setImageResource(R.drawable.ic_not_int)
            emptyText.text = "Проблемы со связью\nЗагрузка не удалась. Проверьте подключение к интернету"
            emptyButton.isVisible = true
        } else {
            emptyIcon.setImageResource(R.drawable.ic_light_mode)
            emptyText.text = "Ничего не нашлось"
            emptyButton.isVisible = false
        }
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra("track", track)
        startActivity(intent)
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
