// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist

import android.app.Application
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.songs.SongsViewModel

/**
 * Created by Wilberforce on 2019-06-07 at 03:38.
 */
class PlaylistSongsViewModel(application: Application) : SongsViewModel(application) {


    override var sortOrder: String? = "${MediaStore.Audio.Media.DATE_ADDED} COLLATE NOCASE ASC"

    fun init(playlistId: Long) {
        uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        init()
    }
}