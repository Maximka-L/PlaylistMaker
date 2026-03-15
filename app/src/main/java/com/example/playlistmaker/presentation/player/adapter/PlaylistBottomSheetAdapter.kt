package com.example.playlistmaker.presentation.player.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBottomSheetBinding
import com.example.playlistmaker.domain.models.Playlist
import java.io.File

class PlaylistBottomSheetAdapter(
    private val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistBottomSheetAdapter.ViewHolder>() {

    private val items = mutableListOf<Playlist>()

    fun submitList(playlists: List<Playlist>) {
        items.clear()
        items.addAll(playlists)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlaylistBottomSheetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(
        private val binding: ItemPlaylistBottomSheetBinding,
        private val onClick: (Playlist) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Playlist) {
            binding.playlistNameText.text = item.name
            binding.playlistCountText.text = when {
                item.tracksCount % 10 == 1 && item.tracksCount % 100 != 11 -> "${item.tracksCount} трек"
                item.tracksCount % 10 in 2..4 && item.tracksCount % 100 !in 12..14 -> "${item.tracksCount} трека"
                else -> "${item.tracksCount} треков"
            }

            if (item.coverPath.isNotBlank()) {
                binding.playlistCoverImage.setImageURI(Uri.fromFile(File(item.coverPath)))
            } else {
                binding.playlistCoverImage.setImageResource(R.drawable.ic_playlist_placeholder)
            }

            binding.root.setOnClickListener { onClick(item) }
        }
    }
}