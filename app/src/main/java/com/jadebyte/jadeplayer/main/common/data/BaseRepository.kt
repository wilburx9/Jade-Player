// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.data

import android.app.Application
import android.database.Cursor
import android.net.Uri
import androidx.annotation.WorkerThread

/**
 * Created by Wilberforce on 19/04/2019 at 16:36.
 */
abstract class BaseRepository<T>(private val application: Application) {

    @WorkerThread
    fun loadData(
        uri: Uri,
        projection: Array<String>? = null,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = null
    ): List<T> {
        val results = mutableListOf<T>()
        val cursor = query(uri, projection, selection, selectionArgs, sortOrder)
        cursor?.use {
            while (it.moveToNext()) {
                results.add(transform(it))
            }
        }
        return results
    }

    fun query(
        uri: Uri,
        projection: Array<String>? = null,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = null
    ): Cursor? = application.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)

    abstract fun transform(cursor: Cursor): T


}