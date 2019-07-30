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
open class AlbumsViewModel(application: Application) : BaseViewModel<Album>(application) {

    final override var repository: BaseRepository<Album> = AlbumsRepository(application)

    override var sortOrder: String? = "${MediaStore.Audio.Albums.ALBUM} COLLATE NOCASE ASC"

    override var uri: Uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

    final override var projection: Array<String>? = arrayOf(
        MediaStore.Audio.Albums.ALBUM,
        MediaStore.Audio.Albums.ARTIST,
        MediaStore.Audio.Albums.NUMBER_OF_SONGS,
        MediaStore.Audio.Albums._ID,
        MediaStore.Audio.Albums.FIRST_YEAR
    )

}
