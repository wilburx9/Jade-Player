// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.songs

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.jadebyte.jadeplayer.main.common.data.MediaStoreRepository

/**
 * Created by Wilberforce on 17/04/2019 at 04:12.
 */
open class SongsRepository(application: Application) : MediaStoreRepository<Song>(application) {

    override fun transform(cursor: Cursor): Song = Song(cursor)

    @WorkerThread
    fun fetchMatchingIds(
        uri: Uri,
        projection: Array<String>? = null,
        selection: String? = null,
        selectionArgs: Array<String>? = null
    ): List<Long> {
        val results = mutableListOf<Long>()
        val cursor = query(uri, projection, selection, selectionArgs)
        cursor?.use {
            while (it.moveToNext()) {
                results.add(it.getLong(it.getColumnIndex(MediaStore.Audio.Media._ID)))
            }
        }
        return results
    }
}