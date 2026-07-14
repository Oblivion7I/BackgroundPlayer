package com.afm.backgroundmusicplayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afm.backgroundmusicplayer.core.domain.model.Song
import com.afm.backgroundmusicplayer.core.domain.preferences.PlayerPreferences
import com.afm.backgroundmusicplayer.core.domain.repository.MusicRepository
import com.afm.backgroundmusicplayer.core.player.PlaybackManager
import com.afm.backgroundmusicplayer.core.player.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val repository: MusicRepository,
    private val playbackManager: PlaybackManager,
    private val preferences: PlayerPreferences
) : ViewModel() {

    val playerState = playbackManager.playerState

    private val _isDarkMode = MutableStateFlow(preferences.isDarkMode())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isDynamicColor = MutableStateFlow(preferences.isDynamicColor())
    val isDynamicColor: StateFlow<Boolean> = _isDynamicColor.asStateFlow()

    private val _autoResume = MutableStateFlow(preferences.isAutoResume())
    val autoResume: StateFlow<Boolean> = _autoResume.asStateFlow()

    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            val songs = repository.getSongsFromAssets()
            playbackManager.setPlaylist(songs)
            val speed = preferences.getPlaybackSpeed()
            playbackManager.setPlaybackSpeed(speed)
        }
    }

    fun play() = playbackManager.play()
    fun pause() = playbackManager.pause()
    fun playPrevious() = playbackManager.playPrevious()
    fun playNext() = playbackManager.playNext()
    fun togglePlayPause() = playbackManager.togglePlayPause()
    fun seekTo(position: Long) = playbackManager.seekTo(position)
    fun toggleShuffle() = playbackManager.toggleShuffle()
    fun toggleRepeat() = playbackManager.toggleRepeat()
    fun setPlaybackSpeed(speed: Float) = playbackManager.setPlaybackSpeed(speed)
    fun setVolume(volume: Float) = playbackManager.setVolume(volume)

    fun toggleDarkMode() {
        val updated = !preferences.isDarkMode()
        preferences.setDarkMode(updated)
        _isDarkMode.value = updated
    }

    fun toggleDynamicColor() {
        val updated = !preferences.isDynamicColor()
        preferences.setDynamicColor(updated)
        _isDynamicColor.value = updated
    }

    fun toggleAutoResume() {
        val updated = !preferences.isAutoResume()
        preferences.setAutoResume(updated)
        _autoResume.value = updated
    }
}