// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.albums

import android.app.Application
import android.database.Cursor
import com.jadebyte.jadeplayer.main.common.data.BaseMediaStoreRepository

/**
 * Created by Wilberforce on 19/04/2019 at 16:33.
 */
class AlbumsRepository(application: Application) : BaseMediaStoreRepository<Album>(application) {

    override fun transform(cursor: Cursor): Album = Album(cursor)


}