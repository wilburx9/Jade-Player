// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.artists

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.common.data.BaseRepository
import com.jadebyte.jadeplayer.main.common.view.BaseViewModel

/**
 * Created by Wilberforce on 2019-04-25 at 00:57.
 */
open class ArtistsViewModel(application: Application) : BaseViewModel<Artist>(application) {
    final override var repository: BaseRepository<Artist> = ArtistsRepository(application)

    override var sortOrder: String? = "${MediaStore.Audio.Albums.ARTIST} COLLATE NOCASE ASC"

    override var uri: Uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

    final override var projection: Array<String>? = arrayOf(
        MediaStore.Audio.Artists.ARTIST,
        MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
        MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
        MediaStore.Audio.Artists._ID
    )
}
