package com.example.playlistmaker

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TracksAdapter (
    var dataset: List<Track>
) : RecyclerView.Adapter<TracksAdapter.TrackViewHolder>() {

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
                .transform(RoundedCorners(dpToPx(2f, itemView)))
                .into(ivArtwork)
        }
        private fun dpToPx(dp: Float, context: View): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics).toInt()
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
    }

}