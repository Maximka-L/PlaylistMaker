package com.example.playlistmaker.presentation.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track


class TracksAdapter(
    private var dataset: List<Track>,
    private val onItemClick: (Track) -> Unit
) : RecyclerView.Adapter<TracksAdapter.TrackViewHolder>() {

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivArtwork: ImageView = itemView.findViewById(R.id.ivArtwork)
        private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
        private val tvArtistName: TextView = itemView.findViewById(R.id.tvArtistName)
        private val tvTrackTime: TextView = itemView.findViewById(R.id.tvTrackTime)

        fun bind(track: Track) {
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            tvTrackTime.text = track.trackTime

            Glide.with(itemView)
                .load(track.artworkUrl100)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.ic_placeholder)
                .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.corner_radius_small)))
                .into(ivArtwork)

            // Обработка клика
            itemView.setOnClickListener {
                onItemClick(track)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(dataset[position])
    }

    override fun getItemCount(): Int = dataset.size

    fun updateDataset(dataset: List<Track>) {
        this.dataset = dataset
        notifyDataSetChanged()
    }
}