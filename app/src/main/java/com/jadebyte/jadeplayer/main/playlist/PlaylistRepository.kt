// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist

import android.app.Application
import android.database.Cursor
import com.jadebyte.jadeplayer.main.common.data.BaseRepository

/**
 * Created by Wilberforce on 2019-05-19 at 10:24.
 */
class PlaylistRepository(application: Application) : BaseRepository<Playlist>(application) {
    override fun transform(cursor: Cursor): Playlist = Playlist(cursor)
}