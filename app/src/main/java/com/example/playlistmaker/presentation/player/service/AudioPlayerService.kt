package com.example.playlistmaker.presentation.player.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playlistmaker.R
import android.content.pm.ServiceInfo

class AudioPlayerService : Service(), PlayerServiceController {

    private val binder = PlayerBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var listener: PlayerStateListener? = null

    private var artistName: String = ""
    private var trackName: String = ""

    inner class PlayerBinder : Binder() {
        fun getService(): PlayerServiceController = this@AudioPlayerService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        artistName = intent?.getStringExtra(EXTRA_ARTIST) ?: ""
        trackName = intent?.getStringExtra(EXTRA_TRACK) ?: ""
        return binder
    }

    override fun prepare(url: String, artistName: String, trackName: String) {
        this.artistName = artistName
        this.trackName = trackName
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                listener?.onPaused()
            }
            setOnCompletionListener {
                listener?.onCompleted()
                hideForeground()
            }
            prepareAsync()
        }
    }

    override fun play() {
        mediaPlayer?.start()
        listener?.onPlaying()
    }

    override fun pause() {
        mediaPlayer?.pause()
        listener?.onPaused()
    }

    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    override fun currentPositionMs(): Int = mediaPlayer?.currentPosition ?: 0

    override fun setOnPlayerStateListener(listener: PlayerStateListener) {
        this.listener = listener
    }

    override fun showForeground() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText("$artistName - $trackName")
            .setSmallIcon(R.drawable.ic_app)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, 0)
        }
    }

    override fun hideForeground() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Player",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "player_channel"
        const val NOTIFICATION_ID = 1
        const val EXTRA_ARTIST = "extra_artist"
        const val EXTRA_TRACK = "extra_track"
    }
}