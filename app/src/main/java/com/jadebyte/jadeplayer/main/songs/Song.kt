// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.songs

import android.database.Cursor
import android.os.Parcelable
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.common.utils.GeneralUtils.getTrackNumber
import com.jadebyte.jadeplayer.main.common.utils.ImageUtils
import kotlinx.android.parcel.Parcelize


/**
 * Created by Wilberforce on 16/04/2019 at 01:22.
 */

@Parcelize
data class Song(
    val title: String,
    val album: Album,
    val path: String,
    val mediaId: Long,
    val albumId: Long,
    val duration: Long,
    val number: String,
    val artPath: String,
    val artistId: Long
) : Parcelable {
    constructor(cursor: Cursor) : this(
        title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
        album = Album(cursor, true),
        path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
        mediaId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
        albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
        duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)),
        artistId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)),
        artPath = ImageUtils.getAlbumArtUri(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))).toString(),
        number = getTrackNumber(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)))
    )


}