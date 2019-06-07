// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.common.data.BaseRepository
import com.jadebyte.jadeplayer.main.common.data.BaseViewModel

/**
 * Created by Wilberforce on 2019-05-19 at 10:11.
 */
class PlaylistViewModel(application: Application) : BaseViewModel<Playlist>(application) {
    final override var repository: BaseRepository<Playlist> = PlaylistRepository(application)

    override var selection: String? = null

    override var selectionArgs: Array<String>? = null

    override var sortOrder: String? = "${MediaStore.Audio.Playlists.DATE_ADDED} COLLATE NOCASE DESC"

    override var uri: Uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI

    final override var projection: Array<String>? = arrayOf(
        MediaStore.Audio.Playlists._ID,
        MediaStore.Audio.Playlists.NAME,
        MediaStore.Audio.Playlists.DATE_ADDED
    )
}