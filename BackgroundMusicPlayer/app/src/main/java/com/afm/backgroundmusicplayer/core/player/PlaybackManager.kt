package com.afm.backgroundmusicplayer.core.player

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.afm.backgroundmusicplayer.core.domain.model.Song
import com.afm.backgroundmusicplayer.core.domain.preferences.PlayerPreferences
import com.afm.backgroundmusicplayer.core.service.MusicService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackManager @Inject constructor(
    private val context: Context,
    private val preferences: PlayerPreferences
) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

    init {
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).build()
        controllerFuture?.addListener({
            controller = controllerFuture?.get()
            setupControllerListener()
        }, MoreExecutors.directExecutor())
    }

    private fun setupControllerListener() {
        val currentController = controller ?: return
        
        currentController.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) {
                    startProgressTracker()
                } else {
                    stopProgressTracker()
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val currentId = mediaItem?.mediaId
                val matchedSong = _playerState.value.playList.find { it.id == currentId }
                _playerState.update {
                    it.copy(
                        currentSong = matchedSong,
                        duration = currentController.duration.coerceAtLeast(0L)
                    )
                }
                preferences.saveLastPlayedSongId(currentId)
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                _playerState.update { it.copy(playbackSpeed = playbackParameters.speed) }
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _playerState.update { it.copy(isShuffleEnabled = shuffleModeEnabled) }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                _playerState.update {
                    it.copy(isRepeatEnabled = repeatMode != Player.REPEAT_MODE_OFF)
                }
            }
        })

        _playerState.update {
            it.copy(
                isShuffleEnabled = currentController.shuffleModeEnabled,
                isRepeatEnabled = currentController.repeatMode != Player.REPEAT_MODE_OFF,
                playbackSpeed = currentController.playbackParameters.speed,
                volume = currentController.volume
            )
        }
    }

    fun setPlaylist(songs: List<Song>) {
        _playerState.update { it.copy(playList = songs) }
        val controller = this.controller ?: return
        controller.clearMediaItems()
        val mediaItems = songs.map { song ->
            MediaItem.Builder()
                .setMediaId(song.id)
                .setUri("asset:///${song.assetPath}")
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .build()
                )
                .build()
        }
        controller.setMediaItems(mediaItems)
        controller.prepare()

        val lastPlayedId = preferences.getLastPlayedSongId()
        if (lastPlayedId != null) {
            val index = songs.indexOfFirst { it.id == lastPlayedId }
            if (index != -1) {
                controller.seekTo(index, 0)
                if (preferences.isAutoResume()) {
                    controller.play()
                }
            }
        }
    }

    fun play() {
        controller?.play()
    }

    fun pause() {
        controller?.pause()
    }

    fun playPrevious() {
        controller?.seekToPrevious()
    }

    fun playNext() {
        controller?.seekToNext()
    }

    fun togglePlayPause() {
        val controller = this.controller ?: return
        if (controller.isPlaying) {
            controller.pause()
        } else {
            controller.play()
        }
    }

    fun seekTo(position: Long) {
        controller?.seekTo(position)
        _playerState.update { it.copy(currentPosition = position) }
    }

    fun toggleShuffle() {
        val controller = this.controller ?: return
        controller.shuffleModeEnabled = !controller.shuffleModeEnabled
    }

    fun toggleRepeat() {
        val controller = this.controller ?: return
        val currentMode = controller.repeatMode
        controller.repeatMode = if (currentMode == Player.REPEAT_MODE_OFF) {
            Player.REPEAT_MODE_ALL
        } else {
            Player.REPEAT_MODE_OFF
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        val controller = this.controller ?: return
        controller.playbackParameters = PlaybackParameters(speed)
        preferences.savePlaybackSpeed(speed)
    }

    fun setVolume(volume: Float) {
        val controller = this.controller ?: return
        controller.volume = volume
        _playerState.update { it.copy(volume = volume) }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (true) {
                controller?.let { currentController ->
                    _playerState.update {
                        it.copy(
                            currentPosition = currentController.currentPosition,
                            duration = currentController.duration.coerceAtLeast(0L)
                        )
                    }
                }
                delay(500)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
    }
}