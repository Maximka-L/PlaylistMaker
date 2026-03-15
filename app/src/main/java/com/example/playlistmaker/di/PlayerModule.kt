package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.data.player.AndroidAudioPlayer
import com.example.playlistmaker.domain.player.AudioPlayer
import org.koin.dsl.module
import com.example.playlistmaker.data.repository.PlaylistRepositoryImpl
import com.example.playlistmaker.domain.repository.PlaylistRepository

val playerModule = module {


    factory { MediaPlayer() }

    factory<AudioPlayer> { AndroidAudioPlayer(get()) }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(
            playlistDao = get(),
            playlistTrackDao = get()
        )
    }
}
