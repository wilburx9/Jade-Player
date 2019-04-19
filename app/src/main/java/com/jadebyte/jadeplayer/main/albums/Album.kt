// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.albums

import android.database.Cursor
import android.provider.MediaStore

/**
 * Created by Wilberforce on 16/04/2019 at 00:49.
 */
data class Album(val name: String, val artist: String, val tracks: Int, val id: Long) {

    constructor(data: Cursor) : this(
        name = data.getString(data.getColumnIndex(MediaStore.Audio.Albums.ALBUM)),
        artist = data.getString(data.getColumnIndex(MediaStore.Audio.Albums.ARTIST)),
        tracks = data.getInt(data.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)),
        id = data.getLong(data.getColumnIndex(MediaStore.Audio.Albums._ID))
    )
}
