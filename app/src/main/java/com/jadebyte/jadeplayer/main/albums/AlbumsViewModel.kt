// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.albums

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.common.data.BaseRepository
import com.jadebyte.jadeplayer.main.common.data.BaseViewModel

/**
 * Created by Wilberforce on 19/04/2019 at 16:34.
 */
open class AlbumsViewModel(application: Application) : BaseViewModel<Album>(application, uri) {

    override var repository: BaseRepository<Album> = AlbumsRepository(application, uri)

    override var selection: String? = null

    override var selectionArgs: Array<String>? = null

    override var sortOrder: String? = "${MediaStore.Audio.Albums.ALBUM} COLLATE NOCASE ASC"

    override var projection: Array<String>? = arrayOf(
        MediaStore.Audio.Albums.ALBUM,
        MediaStore.Audio.Albums.ARTIST,
        MediaStore.Audio.Albums.NUMBER_OF_SONGS,
        MediaStore.Audio.Albums._ID
    )

}

var uri: Uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI