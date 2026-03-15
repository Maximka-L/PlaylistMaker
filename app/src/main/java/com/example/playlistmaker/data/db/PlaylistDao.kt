package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface PlaylistDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlayList(playlist: PlaylistEntity)

    @Query("SELECT * FROM playLists ORDER BY id DESC")
    fun getPlaylists(): Flow<List<PlaylistEntity>>
}