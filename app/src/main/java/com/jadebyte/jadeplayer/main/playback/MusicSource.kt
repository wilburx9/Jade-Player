// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.
// A little modification was made from the original file: https://raw.githubusercontent.com/googlesamples/android-UniversalMusicPlayer/master/common/src/main/java/com/example/android/uamp/media/library/MusicSource.kt

package com.jadebyte.jadeplayer.main.playback

import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import androidx.annotation.IntDef
import com.jadebyte.jadeplayer.common.contains


/**
 * Created by Wilberforce on 2019-08-19 at 22:09.
 *
 * Interface used by [PlaybackService] for looking up [MediaMetadataCompat] objects
 *
 *  Because Kotlin provides methods such [Iterable.find] and [Iterable.filter],
 *  this is a convenient interface to have on sources.
 */
interface MusicSource : Iterable<MediaMetadataCompat> {

    /**
     *  Begins loading the data for this music source.
     */
    suspend fun load()

    /**
     *  Method which will perform a given action after this [MusicSource] is ready to be used.
     *
     *  @param function A lambda expression to be called with a boolean parameter when the source is ready. `true`
     *  indicates the source was successfully prepared., `false` indicates an error occurred.
     */
    fun whenReady(function: (Boolean) -> Unit): Boolean

    /**
     * Handles searching a [MusicSource] from a focused voice search, often coming
     * from the Google Assistant.
     */
    fun search(query: String, bundle: Bundle): List<MediaMetadataCompat>
}

@IntDef(
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
)
@Retention(AnnotationRetention.SOURCE)
annotation class State

/**
 *  State indicating the source was created, but no initialization has performed.
 */
const val STATE_CREATED = 1

/**
 * State indicating initialization of the source is in progress
 */
const val STATE_INITIALIZING = 2

/**
 * State indicating the source has been initialized and is ready to be used
 */
const val STATE_INITIALIZED = 3

/**
 * State indicating an error has occured
 */
const val STATE_ERROR = 4

/**
 * Base class for music sources
 */
abstract class AbstractMusicSource : MusicSource {

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    @State
    var state: Int = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach {
                        it(state == STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    /**
     * Perform an action when this MusicSource is ready.
     *
     * This method is *not* threadsafe. Ensure actions and state changes are only performed on a single thread
     */
    override fun whenReady(function: (Boolean) -> Unit): Boolean {
        return when (state) {
            STATE_CREATED, STATE_INITIALIZING -> {
                onReadyListeners += function
                false
            }
            else -> {
                function(state != STATE_ERROR)
                true
            }
        }
    }

    override fun search(query: String, bundle: Bundle): List<MediaMetadataCompat> {
        // First attempt to search with the "focus" that's provided in the bundle
        val focusSearchResult = when (bundle[MediaStore.EXTRA_MEDIA_FOCUS]) {
            MediaStore.Audio.Genres.ENTRY_CONTENT_TYPE -> {
                // For a Genre focused search, only genre is set.
                val genre = bundle[MediaStore.EXTRA_MEDIA_GENRE]
                filter {
                    it.genre == genre
                }
            }
            MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE -> {
                // For an Artist focused search, only the artist is set.
                val artist = bundle[MediaStore.EXTRA_MEDIA_ARTIST]
                filter {
                    (it.artist == artist || it.albumArtist == artist)
                }
            }
            MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE -> {
                // For an Album focused search, album and artist are set.
                val artist = bundle[MediaStore.EXTRA_MEDIA_ARTIST]
                val album = bundle[MediaStore.EXTRA_MEDIA_ALBUM]
                filter {
                    (it.artist == artist || it.albumArtist == artist) && it.album == album
                }
            }
            MediaStore.Audio.Media.ENTRY_CONTENT_TYPE -> {
                // For a Song (aka Media) focused search, title, album, and artist are set.
                val title = bundle[MediaStore.EXTRA_MEDIA_TITLE]
                val album = bundle[MediaStore.EXTRA_MEDIA_ALBUM]
                val artist = bundle[MediaStore.EXTRA_MEDIA_ARTIST]
                filter {
                    (it.artist == artist || it.albumArtist == artist) && it.album == album
                            && it.title == title
                }
            }
            else -> {
                // There isn't a focus, so no results yet.
                emptyList()
            }
        }

        // Check if we found any results from the focused search
        if (focusSearchResult.isNotEmpty()) return focusSearchResult

        // The query can be null if the user asked to "play music", or something similar.
        // Let's just return them all, shuffled as something to play
        if (query.isBlank()) return shuffled()

        // Let's check check the query against a few fields
        return filter {
            it.title.contains(query)
                    || it.genre.contains(query)
                    || it.artist.contains(query)
                    || it.album.contains(query)
                    || it.author.contains(query)
                    || it.composer.contains(query)
                    || it.composer.contains(query)
        }
    }
}


