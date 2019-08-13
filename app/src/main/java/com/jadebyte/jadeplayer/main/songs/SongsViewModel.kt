// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.songs

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.common.data.BaseRepository
import com.jadebyte.jadeplayer.main.common.data.BaseViewModel

/**
 * Created by Wilberforce on 19/04/2019 at 15:06.
 */
open class SongsViewModel(application: Application) : BaseViewModel<Song>(application) {

    override var repository: BaseRepository<Song> = SongsRepository(application)

    override var selection: String? = "${MediaStore.Audio.Media.IS_MUSIC} != ?"

    override var selectionArgs: Array<String>? = arrayOf("0")

    // Sort with the title in ascending case-insensitive order
    override var sortOrder: String? = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"

    override var uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    override var projection: Array<String>? = basicSongsProjection
}

val basicSongsProjection = arrayOf(
    MediaStore.Audio.Media.TITLE,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.ALBUM_ID,
    MediaStore.Audio.Media.ALBUM,
    MediaStore.Audio.Media.DURATION,
    MediaStore.Audio.Media.DATA,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.TRACK,
    MediaStore.Audio.Media.ALBUM_ID,
    MediaStore.Audio.Media.ARTIST_ID,
    MediaStore.Audio.Media.TITLE_KEY,
    MediaStore.Audio.Media.ALBUM_KEY,
    MediaStore.Audio.Media._ID
)