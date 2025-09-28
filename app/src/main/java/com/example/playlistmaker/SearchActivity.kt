package com.example.playlistmaker

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
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
import kotlinx.coroutines.launch
import java.io.IOException

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var toolbar: Toolbar
    private lateinit var infoBlock: LinearLayout
    private lateinit var emptyIcon: ImageView
    private lateinit var emptyText: TextView
    private lateinit var emptyButton: Button

    private var searchQuery = ""

    private lateinit var adapter: TracksAdapter

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

        val recycler = findViewById<RecyclerView>(R.id.recycler)
        adapter = TracksAdapter(listOf())
        recycler.adapter = adapter

        toolbar.setNavigationOnClickListener { finish() }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            searchEditText.clearFocus()
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        searchEditText.doOnTextChanged { text, _, _, _ ->
            val query = text?.toString()?.trim().orEmpty()

            if (query.isEmpty()) {
                adapter.updateDataset(emptyList())
                adapter.notifyDataSetChanged()
                showEmptyState(false)
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
            } else {
                false
            }
        }

        emptyButton.setOnClickListener {
            if (searchQuery.isNotEmpty()) {
                performSearch(searchQuery)
            } else {

                performSearch("")
            }
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
    }

    private fun showEmptyState(show: Boolean, text: String = "", showButton: Boolean = false) {
        infoBlock.visibility = if (show) View.VISIBLE else View.GONE
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_QUERY", searchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString("SEARCH_QUERY", "") ?: ""
        searchEditText.setText(searchQuery)
        if (searchQuery.isNotEmpty()) {
            performSearch(searchQuery)
        }
    }

    private fun performSearch(query: String) {
        searchQuery = query


        showEmptyState(true, "Загрузка...")



        lifecycleScope.launch {
            try {
                val response = NetworkClient.api.searchSongs(query)
                val tracks = response.results.map { it.toTrack() }

                val filteredTracks = tracks.filter { it.trackName.lowercase().contains(query.toString().lowercase()) || it.artistName.lowercase().contains(query.toString().lowercase()) }

                adapter.updateDataset(filteredTracks)

                adapter.updateDataset(filteredTracks)
                adapter.notifyDataSetChanged()

                if (filteredTracks.isEmpty()) {
                    showEmptyState(true, "Ничего не нашлось")
                } else {
                    showEmptyState(false)
                }

            } catch (e: IOException) {

                showEmptyState(
                    true,
                    "Проблемы со связью\n Загрузка не удалась. Проверьте подключение к интернету",
                    showButton = true
                )
                adapter.updateDataset(emptyList())
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {

                showEmptyState(true, "Произошла ошибка")
                adapter.updateDataset(emptyList())
                adapter.notifyDataSetChanged()
            }

        }
    }
}
