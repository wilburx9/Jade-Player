// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.artists

import android.app.Application
import android.database.Cursor
import android.net.Uri
import com.jadebyte.jadeplayer.main.common.data.BaseRepository

/**
 * Created by Wilberforce on 2019-04-25 at 00:55.
 */
class ArtistsRepository(application: Application, uri: Uri) : BaseRepository<Artist>(application, uri) {
    override fun transform(cursor: Cursor): Artist = Artist(cursor)
}