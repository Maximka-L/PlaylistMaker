package com.example.playlistmaker.presentation.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.presentation.player.AudioPlayerActivity
import com.example.playlistmaker.presentation.search.adapter.TracksAdapter
import com.example.playlistmaker.R
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
    private lateinit var viewModel: SearchViewModel

    private var lastClickTime = 0L
    private val clickDelay = 800L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.searchRoot)) { view, insets ->
            view.updatePadding(top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top)
            insets
        }

        initViews()
        initViewModel()
        observeState()

        toolbar.setNavigationOnClickListener { finish() }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard()
            viewModel.showHistory()
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        searchEditText.doOnTextChanged { text, _, _, _ ->
            val query = text.toString().trim()
            clearButton.isVisible = query.isNotEmpty()
            viewModel.search(query)
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) viewModel.search(query)
                true
            } else false
        }

        viewModel.showHistory()
    }

    private fun initViewModel() {
        viewModel = SearchViewModel(
            com.example.playlistmaker.di.Creator.provideSearchTracksUseCase(this),
            com.example.playlistmaker.di.Creator.provideManageSearchHistoryUseCase(this)
        )
    }

    private fun observeState() {
        viewModel.state.observe(this) { state ->
            when (state) {
                is SearchViewModel.SearchState.Loading -> showLoading(true)
                is SearchViewModel.SearchState.Content -> {
                    adapter.updateDataset(state.data)
                    showEmptyState(false)
                }
                is SearchViewModel.SearchState.Empty -> showEmptyState(true, "Ничего не нашлось")
                is SearchViewModel.SearchState.History -> {
                    adapter.updateDataset(state.data)
                    showHistory()
                }
                is SearchViewModel.SearchState.Error -> showEmptyState(true, state.message, true)
            }
        }
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

        val history =
            com.example.playlistmaker.di.Creator.provideManageSearchHistoryUseCase(this).getHistory()

        adapter = TracksAdapter(history) { track ->
            val time = System.currentTimeMillis()
            if (time - lastClickTime < clickDelay) return@TracksAdapter
            lastClickTime = time

            viewModel.addToHistory(track)
            startActivity(
                Intent(this, AudioPlayerActivity::class.java)
                    .putExtra("track", track)
            )
        }

        recycler.adapter = adapter
    }

    private fun hideKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun showHistory() {
        val history = viewModel.getHistory()
        adapter.updateDataset(history)


        historyBlock.isVisible = history.isNotEmpty()
        recycler.isVisible = history.isNotEmpty()
        youSearchedText.isVisible = history.isNotEmpty()
        clearHistoryButton.isVisible = history.isNotEmpty()
        infoBlock.isVisible = false
        progressBar.isVisible = false
    }

    private fun showEmptyState(show: Boolean, text: String = "", button: Boolean = false) {
        infoBlock.isVisible = show
        emptyIcon.isVisible = show
        emptyText.isVisible = show
        emptyButton.isVisible = button

        recycler.isVisible = !show
        historyBlock.isVisible = !show
        progressBar.isVisible = false

        if (show) emptyText.text = text
    }

    private fun showLoading(show: Boolean) {
        progressBar.isVisible = show
        recycler.isVisible = !show
        historyBlock.isVisible = !show
        infoBlock.isVisible = false
    }
}
