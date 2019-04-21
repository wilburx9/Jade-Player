// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.albums

import android.app.Application
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.songs.SongsViewModel


/**
 * Created by Wilberforce on 2019-04-21 at 12:47.
 */
class AlbumSongsViewModel(application: Application) : SongsViewModel(application) {

    override var selection: String? =
        "${MediaStore.Audio.Media.IS_MUSIC} != ? AND ${MediaStore.Audio.Media.ALBUM_ID} = ?"

    override var sortOrder: String? = "${MediaStore.Audio.Media.TRACK} COLLATE NOCASE ASC"

    fun init(albumId: Long) {
        selectionArgs = arrayOf("0", albumId.toString())
        init()
    }
}