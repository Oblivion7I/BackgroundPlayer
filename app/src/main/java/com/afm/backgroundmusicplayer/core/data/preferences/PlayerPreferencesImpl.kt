package com.afm.backgroundmusicplayer.core.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.afm.backgroundmusicplayer.core.domain.preferences.PlayerPreferences
import javax.inject.Inject

class PlayerPreferencesImpl @Inject constructor(
    context: Context
) : PlayerPreferences {

    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(
        "afm_player_preferences",
        Context.MODE_PRIVATE
    )

    override fun setDarkMode(enabled: Boolean) {
        sharedPrefs.edit().putBoolean("dark_mode", enabled).apply()
    }

    override fun isDarkMode(): Boolean {
        return sharedPrefs.getBoolean("dark_mode", true)
    }

    override fun setDynamicColor(enabled: Boolean) {
        sharedPrefs.edit().putBoolean("dynamic_color", enabled).apply()
    }

    override fun isDynamicColor(): Boolean {
        return sharedPrefs.getBoolean("dynamic_color", true)
    }

    override fun setAutoResume(enabled: Boolean) {
        sharedPrefs.edit().putBoolean("auto_resume", enabled).apply()
    }

    override fun isAutoResume(): Boolean {
        return sharedPrefs.getBoolean("auto_resume", false)
    }

    override fun saveLastPlayedSongId(songId: String?) {
        sharedPrefs.edit().putString("last_song_id", songId).apply()
    }

    override fun getLastPlayedSongId(): String? {
        return sharedPrefs.getString("last_song_id", null)
    }

    override fun savePlaybackSpeed(speed: Float) {
        sharedPrefs.edit().putFloat("playback_speed", speed).apply()
    }

    override fun getPlaybackSpeed(): Float {
        return sharedPrefs.getFloat("playback_speed", 1.0f)
    }
}