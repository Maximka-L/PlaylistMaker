package com.example.playlistmaker.presentation.media.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.domain.models.Playlist
import java.io.File

class PlayListAdapter(
    private val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlayListAdapter.PlayListViewHolder>() {

    private val items = mutableListOf<Playlist>()

    fun submitList(playlists: List<Playlist>) {
        items.clear()
        items.addAll(playlists)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {
        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlayListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayListViewHolder, position: Int) {
        holder.bind(items[position], onClick)
    }

    override fun getItemCount(): Int = items.size

    class PlayListViewHolder(
        private val binding: ItemPlaylistBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Playlist, onClick: (Playlist) -> Unit) {
            binding.playlistNameText.text = item.name
            binding.playlistCountText.text = getTrackCountText(item.tracksCount)

            if (item.coverPath.isNotBlank()) {
                binding.playlistCoverImage.setImageURI(
                    Uri.fromFile(File(item.coverPath))
                )
            } else {
                binding.playlistCoverImage.setImageResource(
                    R.drawable.ic_playlist_placeholder
                )
            }


            itemView.setOnClickListener {
                onClick(item)
            }
        }

        private fun getTrackCountText(count: Int): String {
            return itemView.resources.getQuantityString(
                R.plurals.tracks_count,
                count,
                count
            )
        }
    }
}