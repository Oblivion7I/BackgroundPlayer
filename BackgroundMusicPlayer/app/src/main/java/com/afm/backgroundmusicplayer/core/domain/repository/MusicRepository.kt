package com.afm.backgroundmusicplayer.core.domain.repository

import com.afm.backgroundmusicplayer.core.domain.model.Song

interface MusicRepository {
    suspend fun getSongsFromAssets(): List<Song>
}