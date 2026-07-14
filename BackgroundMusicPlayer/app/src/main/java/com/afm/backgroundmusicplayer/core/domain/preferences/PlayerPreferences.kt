package com.afm.backgroundmusicplayer.core.domain.preferences

interface PlayerPreferences {
    fun setDarkMode(enabled: Boolean)
    fun isDarkMode(): Boolean
    fun setDynamicColor(enabled: Boolean)
    fun isDynamicColor(): Boolean
    fun setAutoResume(enabled: Boolean)
    fun isAutoResume(): Boolean
    fun saveLastPlayedSongId(songId: String?)
    fun getLastPlayedSongId(): String?
    fun savePlaybackSpeed(speed: Float)
    fun getPlaybackSpeed(): Float
}