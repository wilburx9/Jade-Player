// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.songs

import android.app.Application
import android.database.Cursor
import com.jadebyte.jadeplayer.main.common.data.BaseRepository

/**
 * Created by Wilberforce on 17/04/2019 at 04:12.
 */
class SongsRepository(application: Application) : BaseRepository<Song>(application) {

    override fun transform(cursor: Cursor): Song = Song(cursor)

}