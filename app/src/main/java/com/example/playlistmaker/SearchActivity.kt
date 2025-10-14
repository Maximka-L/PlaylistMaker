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
import kotlinx.coroutines.launch
import java.io.IOException

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

    private lateinit var SearchHistory: SearchHistory
    private lateinit var adapter: TracksAdapter

    private var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.searchRoot)) { view, insets ->
            view.updatePadding(top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top)
            insets
        }

        toolbar = findViewById(R.id.toolbar)
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)
        infoBlock = findViewById(R.id.infoblock)
        emptyIcon = findViewById(R.id.empty_icon)
        emptyText = findViewById(R.id.empty_text)
        emptyButton = findViewById(R.id.empty_button)

        youSearchedText = findViewById(R.id.youserchs)
        clearHistoryButton = findViewById(R.id.clear_histors)

        SearchHistory = SearchHistory(applicationContext.getSharedPreferences("DEFAULT", Context.MODE_PRIVATE))

        val recycler = findViewById<RecyclerView>(R.id.recycler)


        adapter = TracksAdapter(SearchHistory.getHistory()) { track ->
            SearchHistory.addTrack(track)
            val intent = Intent(this@SearchActivity, AudioPlayerActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
        }
        recycler.adapter = adapter

        toolbar.setNavigationOnClickListener { finish() }

        // Очистка поля поиска
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            searchEditText.clearFocus()
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        // Очистка истории
        clearHistoryButton.setOnClickListener {
            SearchHistory.clearHistory()
            adapter.updateDataset(emptyList())
            adapter.notifyDataSetChanged()
            youSearchedText.isVisible = false
            clearHistoryButton.isVisible = false
        }

        // Реакция на изменения текста
        searchEditText.doOnTextChanged { text, _, _, _ ->
            val query = text?.toString()?.trim().orEmpty()

            clearButton.isVisible = query.isNotEmpty()

            if (query.isEmpty()) {
                val history = SearchHistory.getHistory()

                if (history.isEmpty()) {
                    adapter.updateDataset(emptyList())
                    youSearchedText.isVisible = false
                    clearHistoryButton.isVisible = false
                    showEmptyState(false)
                } else {
                    adapter.updateDataset(history)
                    youSearchedText.isVisible = true
                    clearHistoryButton.isVisible = true
                    showEmptyState(false)
                }

                adapter.notifyDataSetChanged()
                return@doOnTextChanged
            }

            performSearch(query)
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    performSearch(query)
                }
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
            searchEditText.isEnabled = true
        }

        updateHistoryVisibility()
    }

    private fun showEmptyState(show: Boolean, text: String = "", showButton: Boolean = false) {
        infoBlock.isVisible = show
        emptyIcon.isVisible = show
        emptyText.isVisible = show
        emptyButton.isVisible = showButton

        if (show) {
            emptyIcon.setImageResource(if (showButton) R.drawable.not_int else R.drawable.light_mode)
            emptyText.text = text
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun updateHistoryVisibility() {
        val hasHistory = SearchHistory.getHistory().isNotEmpty()
        youSearchedText.isVisible = hasHistory
        clearHistoryButton.isVisible = hasHistory
    }

    private fun performSearch(query: String) {
        searchQuery = query
        showEmptyState(true, "Загрузка...")

        lifecycleScope.launch {
            try {
                val response = NetworkClient.api.searchSongs(query)
                val tracks = response.results.map { it.toTrack() }

                adapter.updateDataset(tracks)
                adapter.notifyDataSetChanged()

                if (tracks.isEmpty()) {
                    showEmptyState(true, "Ничего не нашлось")
                    youSearchedText.isVisible = false
                    clearHistoryButton.isVisible = false
                } else {
                    showEmptyState(false)
                    youSearchedText.isVisible = false
                    clearHistoryButton.isVisible = false
                }

            } catch (e: IOException) {
                showEmptyState(
                    true,
                    "Проблемы со связью\nЗагрузка не удалась. Проверьте подключение к интернету",
                    showButton = true
                )
                adapter.updateDataset(SearchHistory.getHistory())
                adapter.notifyDataSetChanged()
                updateHistoryVisibility()

            } catch (e: Exception) {
                showEmptyState(true, getString(R.string.error_no_connection), showButton = true)
                adapter.updateDataset(SearchHistory.getHistory())
                adapter.notifyDataSetChanged()
                updateHistoryVisibility()
            }
        }
    }
}