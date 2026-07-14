package com.afm.backgroundmusicplayer.core.domain.model

import android.net.Uri

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val duration: Long,
    val assetPath: String,
    val albumArtUri: Uri? = null
)