// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.albums

import android.database.Cursor
import android.os.Parcelable
import android.provider.MediaStore
import kotlinx.android.parcel.Parcelize

/**
 * Created by Wilberforce on 16/04/2019 at 00:49.
 */
@Parcelize
data class Album(
    val name: String,
    val artist: String,
    val tracks: Int? = 0,
    val id: Long? = 0,
    val year: String? = ""
) : Parcelable {

    constructor(data: Cursor) : this(
        name = data.getString(data.getColumnIndex(MediaStore.Audio.Albums.ALBUM)),
        artist = data.getString(data.getColumnIndex(MediaStore.Audio.Albums.ARTIST)),
        year = data.getString(data.getColumnIndex(MediaStore.Audio.Albums.FIRST_YEAR)),
        tracks = data.getInt(data.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)),
        id = data.getLong(data.getColumnIndex(MediaStore.Audio.Albums._ID))
    )

    constructor(cursor: Cursor, frmSong: Boolean) : this(
        name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
        artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
    )

    /**
     * Overriding because of the key for Glide's custom loader
     * I am not very sure my implementation of the custom loader[ArtistModelLoader] uses this function though.
     * Just taking precautions.
     * We are only using name and artist because [Song] object does't have [tracks] and [id]. So this way the
     * key will be uniform
     **/
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Album

        if (name != other.name) return false
        if (artist != other.artist) return false

        return true
    }

    // Same as equals() function above
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + artist.hashCode()
        return result
    }


}
