// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.songs

import android.database.Cursor
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.common.utils.ImageUtils


/**
 * Created by Wilberforce on 16/04/2019 at 01:22.
 */
data class Song(
    val title: String,
    val artist: String,
    val album: String,
    val path: String,
    val mediaId: Long,
    val albumId: Long,
    val duration: Long,
    val number: Int,
    val artPath: String,
    val artistId: Long
) {
    constructor(cursor: Cursor) : this(
        title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
        artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
        album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
        path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
        mediaId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
        albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
        duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)),
        artistId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)),
        artPath = ImageUtils.getAlbumArtUri(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))).toString(),
        number = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK))
    )
}