package com.example.playlistmaker.presentation.player.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist

class PlaylistBottomSheetViewHolder(
    itemView: View,
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val coverImageView: ImageView = itemView.findViewById(R.id.playlistCoverImage)
    private val nameTextView: TextView = itemView.findViewById(R.id.playlistNameText)
    private val tracksCountTextView: TextView = itemView.findViewById(R.id.playlistCountText)

    fun bind(playlist: Playlist) {
        nameTextView.text = playlist.name
        tracksCountTextView.text = playlist.tracksCount.toString()
        coverImageView.setImageResource(R.drawable.placeholder_cover)

        itemView.setOnClickListener {
            onPlaylistClick(playlist)
        }
    }
}