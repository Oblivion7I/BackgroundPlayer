package com.afm.backgroundmusicplayer.core.data.repository

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.afm.backgroundmusicplayer.core.domain.model.Song
import com.afm.backgroundmusicplayer.core.domain.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val context: Context
) : MusicRepository {

    override suspend fun getSongsFromAssets(): List<Song> = withContext(Dispatchers.IO) {
        val songs = mutableListOf<Song>()
        val assetManager = context.assets
        try {
            val files = assetManager.list("music") ?: emptyArray()
            files.filter { file ->
                val extension = file.substringAfterLast(".", "").lowercase()
                extension in listOf("mp3", "aac", "wav", "flac", "ogg")
            }.forEachIndexed { index, fileName ->
                val assetPath = "music/$fileName"
                val descriptor = assetManager.openFd(assetPath)
                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setDataSource(
                        descriptor.fileDescriptor,
                        descriptor.startOffset,
                        descriptor.length
                    )
                    val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) 
                        ?: fileName.substringBeforeLast(".")
                    val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) 
                        ?: "Unknown Artist"
                    val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    val duration = durationStr?.toLongOrNull() ?: 0L

                    songs.add(
                        Song(
                            id = index.toString(),
                            title = title,
                            artist = artist,
                            duration = duration,
                            assetPath = assetPath,
                            albumArtUri = Uri.parse("android.resource://${context.packageName}/drawable/ic_default_art")
                        )
                    )
                } catch (e: Exception) {
                    songs.add(
                        Song(
                            id = index.toString(),
                            title = fileName.substringBeforeLast("."),
                            artist = "Unknown Artist",
                            duration = 0L,
                            assetPath = assetPath,
                            albumArtUri = null
                        )
                    )
                } finally {
                    retriever.release()
                    descriptor.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        songs
    }
}