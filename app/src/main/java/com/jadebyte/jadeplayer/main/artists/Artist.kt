// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.artists

import android.database.Cursor
import android.os.Parcelable
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.common.data.Model
import kotlinx.android.parcel.Parcelize


/**
 * Created by Wilberforce on 2019-04-25 at 00:46.
 */
@Parcelize
data class Artist(
    override val id: Long,
    val name: String,
    val songsCount: Int,
    val albumsCount: Int
) : Model(),
    Parcelable {

    constructor(data: Cursor) : this(
        name = data.getString(data.getColumnIndex(MediaStore.Audio.Artists.ARTIST)),
        songsCount = data.getInt(data.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)),
        albumsCount = data.getInt(data.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)),
        id = data.getLong(data.getColumnIndex(MediaStore.Audio.Artists._ID))
    )

}