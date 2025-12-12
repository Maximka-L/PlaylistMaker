package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.local.SearchHistoryStorage
import com.example.playlistmaker.data.network.ItunesApi
import com.example.playlistmaker.data.network.NetworkClient
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {


    single { Gson() }

    single {
        androidContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }


    single {
        SearchHistoryStorage(sharedPrefs = get(), gson = get())
    }


    single {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }


    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    single<ItunesApi> {
        get<Retrofit>().create(ItunesApi::class.java)
    }


    single {
        NetworkClient(api = get(), context = androidContext())
    }
}
