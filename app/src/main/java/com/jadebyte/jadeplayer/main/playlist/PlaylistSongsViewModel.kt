// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist

import android.app.Application
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.common.data.MediaStoreRepository
import com.jadebyte.jadeplayer.main.songs.Song
import com.jadebyte.jadeplayer.main.songs.SongsViewModel
import com.jadebyte.jadeplayer.main.songs.baseSongsProjection

/**
 * Created by Wilberforce on 2019-06-07 at 03:38.
 */
class PlaylistSongsViewModel(application: Application) : SongsViewModel(application) {

    override var repository: MediaStoreRepository<Song> = PlaylistSongsRepository(application)

    override var sortOrder: String? = "${MediaStore.Audio.Media.DATE_ADDED} COLLATE NOCASE ASC"

    override var projection: Array<String>? =
        listOf(*baseSongsProjection, MediaStore.Audio.Playlists.Members.AUDIO_ID).toTypedArray()

    override fun init(vararg params: Any?) {
        uri = MediaStore.Audio.Playlists.Members.getContentUri("external", params[0] as Long)
        super.init()
    }
}