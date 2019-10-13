// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.albums

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.common.data.MediaStoreRepository
import com.jadebyte.jadeplayer.main.common.view.BaseMediaStoreViewModel

/**
 * Created by Wilberforce on 19/04/2019 at 16:34.
 */
open class AlbumsViewModel(application: Application) : BaseMediaStoreViewModel<Album>(application) {

    final override var repository: MediaStoreRepository<Album> = AlbumsRepository(application)

    override var sortOrder: String? = "${MediaStore.Audio.Albums.ALBUM} COLLATE NOCASE ASC"

    override var uri: Uri = baseAlbumUri

    final override var projection: Array<String>? = baseAlbumProjection

}

val baseAlbumProjection = arrayOf(
    MediaStore.Audio.Albums.ALBUM,
    MediaStore.Audio.Albums.ARTIST,
    MediaStore.Audio.Albums.NUMBER_OF_SONGS,
    MediaStore.Audio.Albums._ID,
    MediaStore.Audio.Albums.FIRST_YEAR,
    MediaStore.Audio.Albums.ALBUM_KEY
)

val  baseAlbumUri: Uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
