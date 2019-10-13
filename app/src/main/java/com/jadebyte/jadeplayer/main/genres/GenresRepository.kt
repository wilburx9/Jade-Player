// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.genres

import android.app.Application
import android.database.Cursor
import com.jadebyte.jadeplayer.main.common.data.MediaStoreRepository

class GenresRepository(application: Application) : MediaStoreRepository<Genre>(application) {

    override fun transform(cursor: Cursor) = Genre(cursor)
}