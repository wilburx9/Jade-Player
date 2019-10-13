// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.artists

import android.app.Application
import android.database.Cursor
import com.jadebyte.jadeplayer.main.common.data.MediaStoreRepository

/**
 * Created by Wilberforce on 2019-04-25 at 00:55.
 */
class ArtistsRepository(application: Application) : MediaStoreRepository<Artist>(application) {
    override fun transform(cursor: Cursor): Artist = Artist(cursor)
}