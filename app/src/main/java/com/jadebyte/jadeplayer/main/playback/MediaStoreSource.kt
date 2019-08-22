// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * Created by Wilberforce on 2019-08-22 at 01:00.
 *
 * Source of [MediaMetadataCompat] objects created from songs in the MediaStore
 */
class MediaStoreSource(
    val context: Context, val uri: Uri,
    val selection: String,
    val selectionArgs: Array<String>,
    val sortOrder: String
) : AbstractMusicSource() {
    private var catalog: List<MediaMetadataCompat> = emptyList()

    init {
        state = STATE_INITIALIZING

    }

    override suspend fun load() {
        updateCatalog()?.let {
            catalog = it
            state = STATE_INITIALIZED
        } ?: run {
            catalog = emptyList()
            state = STATE_ERROR
        }
    }

    override fun iterator() = catalog.iterator()


    private suspend fun updateCatalog(): List<MediaMetadataCompat>? {
        return withContext(Dispatchers.IO) {
            val results = mutableListOf<MediaMetadataCompat>()
            val cursor = context.contentResolver.query(uri, songsProjection, selection, selectionArgs, sortOrder)
            cursor?.use {
                val count = it.count.toLong()
                while (it.moveToNext()) {
                    results.add(MediaMetadataCompat.Builder().from(it, count).build())
                }
            }
            return@withContext results
        }
    }

}

private val songsProjection = arrayOf(
    MediaStore.Audio.Media.TITLE,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.ALBUM,
    MediaStore.Audio.Media.DURATION,
    MediaStore.Audio.Media.DATA,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.TRACK,
    MediaStore.Audio.Media._ID
)