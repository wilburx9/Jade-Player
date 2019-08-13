// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist

import android.app.Application
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.common.data.BaseRepository
import com.jadebyte.jadeplayer.main.songs.Song
import com.jadebyte.jadeplayer.main.songs.SongsViewModel
import com.jadebyte.jadeplayer.main.songs.basicSongsProjection

/**
 * Created by Wilberforce on 2019-06-07 at 03:38.
 */
class PlaylistSongsViewModel(application: Application) : SongsViewModel(application) {

    override var repository: BaseRepository<Song> = PlaylistSongsRepository(application)

    override var sortOrder: String? = "${MediaStore.Audio.Media.DATE_ADDED} COLLATE NOCASE ASC"

    override var projection: Array<String>? =
        listOf(*basicSongsProjection, MediaStore.Audio.Playlists.Members.AUDIO_ID).toTypedArray()

    fun init(playlistId: Long) {
        uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        init()
    }
}