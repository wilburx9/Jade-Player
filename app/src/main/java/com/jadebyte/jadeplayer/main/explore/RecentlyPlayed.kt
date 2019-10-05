// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.explore

import android.support.v4.media.MediaMetadataCompat
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.jadebyte.jadeplayer.main.common.data.Model
import com.jadebyte.jadeplayer.main.playback.duration
import com.jadebyte.jadeplayer.main.playback.id
import com.jadebyte.jadeplayer.main.playback.mediaUri
import com.jadebyte.jadeplayer.main.playback.title


/**
 * Created by Wilberforce on 2019-09-15 at 06:28.
 */

@Entity(tableName = "recently_played_table")
data class RecentlyPlayed(
    @PrimaryKey
    override val id: String, // media id
    val path: String,
    val artist: String,
    val album: String,
    val title: String,
    val duration: Long,
    val entryDate: Long,
    @Ignore
    var isPlaying: Boolean = false
) : Model() {

    constructor(
        id: String,
        path: String,
        artist: String,
        album: String,
        title: String,
        duration: Long,
        entryDate: Long
    ) : this(id, path, artist, album, title, duration, entryDate, false)

    constructor(meta: MediaMetadataCompat) : this(
        id = meta.id!!,
        path = meta.mediaUri.toString(),
        artist = meta.description.subtitle?.toString() ?: "",
        title = meta.title ?: "",
        album = meta.description.description?.toString() ?: "",
        duration = meta.duration,
        entryDate = System.currentTimeMillis(),
        isPlaying = false
    )
}