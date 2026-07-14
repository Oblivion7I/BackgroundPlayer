package com.afm.backgroundmusicplayer.core.player

import com.afm.backgroundmusicplayer.core.domain.model.Song

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentSong: Song? = null,
    val playList: List<Song> = emptyList(),
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val isShuffleEnabled: Boolean = false,
    val isRepeatEnabled: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val volume: Float = 1.0f
)