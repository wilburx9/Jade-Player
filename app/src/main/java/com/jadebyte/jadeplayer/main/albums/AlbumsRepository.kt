// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.albums

import android.app.Application
import android.database.Cursor
import android.net.Uri
import com.jadebyte.jadeplayer.main.common.data.BaseRepository

/**
 * Created by Wilberforce on 19/04/2019 at 16:33.
 */
class AlbumsRepository(application: Application, uri: Uri) : BaseRepository<Album>(application, uri) {

    override fun transform(cursor: Cursor): Album = Album(cursor)


}