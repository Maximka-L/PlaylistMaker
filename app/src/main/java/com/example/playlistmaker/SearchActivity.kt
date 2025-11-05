package com.example.playlistmaker

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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.utils.NetworkClient
import com.example.playlistmaker.utils.toTrack
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

private const val CLICK_DEBOUNCE_DELAY = 1000L

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

    private lateinit var searchHistory: SearchHistory
    private lateinit var adapter: TracksAdapter

    private var searchQuery = ""
    private var searchJob: Job? = null
    private var lastClickTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.searchRoot)) { view, insets ->
            view.updatePadding(top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top)
            insets
        }

        // Инициализация вьюшек
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

        searchHistory = SearchHistory(
            applicationContext.getSharedPreferences("DEFAULT", Context.MODE_PRIVATE)
        )

        adapter = TracksAdapter(searchHistory.getHistory()) { track ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < CLICK_DEBOUNCE_DELAY) return@TracksAdapter
            lastClickTime = currentTime

            searchHistory.addTrack(track)
            val intent = Intent(this@SearchActivity, AudioPlayerActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
        }
        recycler.adapter = adapter

        toolbar.setNavigationOnClickListener { finish() }


        clearButton.setOnClickListener {
            searchEditText.text.clear()
            searchEditText.clearFocus()
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(searchEditText.windowToken, 0)
            showHistory()
        }


        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            showHistory()
        }


        searchEditText.doOnTextChanged { text, _, _, _ ->
            val query = text?.toString()?.trim().orEmpty()
            clearButton.isVisible = query.isNotEmpty()

            searchJob?.cancel()

            if (query.isEmpty()) {
                showHistory()
                return@doOnTextChanged
            }

            searchJob = lifecycleScope.launch {
                delay(2000L)
                performSearch(query)
            }
        }


        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) performSearch(query)
                true
            } else false
        }


        if (!isNetworkAvailable()) {
            showEmptyState(
                true,
                "Проблемы со связью\nЗагрузка не удалась. Проверьте подключение к интернету",
                showButton = true
            )
        } else {
            showEmptyState(false)
            showHistory()
        }
    }

    private fun showHistory() {
        val history = searchHistory.getHistory()
        adapter.updateDataset(history)
        adapter.notifyDataSetChanged()

        historyBlock.isVisible = history.isNotEmpty()
        recycler.isVisible = history.isNotEmpty()
        youSearchedText.isVisible = history.isNotEmpty()
        clearHistoryButton.isVisible = history.isNotEmpty()
        infoBlock.isVisible = false
        progressBar.isVisible = false
    }

    private fun showEmptyState(show: Boolean, text: String = "", showButton: Boolean = false) {
        infoBlock.isVisible = show
        emptyIcon.isVisible = show
        emptyText.isVisible = show
        emptyButton.isVisible = showButton
        historyBlock.isVisible = !show
        recycler.isVisible = !show
        progressBar.isVisible = false

        if (show) {
            emptyIcon.setImageResource(if (showButton) R.drawable.not_int else R.drawable.light_mode)
            emptyText.text = text
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.isVisible = show
        historyBlock.isVisible = !show
        recycler.isVisible = !show
        infoBlock.isVisible = !show
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun performSearch(query: String) {
        searchQuery = query
        showEmptyState(false)
        showLoading(true)

        lifecycleScope.launch {
            try {
                val response = NetworkClient.api.searchSongs(query)
                val tracks = response.results.map { it.toTrack() }

                if (tracks.isEmpty()) {

                    adapter.updateDataset(emptyList())
                    showEmptyState(true, "Ничего не нашлось")
                } else {

                    adapter.updateDataset(tracks)
                    showEmptyState(false)
                }

                adapter.notifyDataSetChanged()

            } catch (e: IOException) {

                adapter.updateDataset(searchHistory.getHistory())
                showEmptyState(
                    true,
                    "Проблемы со связью\nЗагрузка не удалась. Проверьте подключение к интернету",
                    showButton = true
                )

            } finally {

                progressBar.isVisible = false
            }
        }
    }

}
