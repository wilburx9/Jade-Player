// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.data

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.WorkerThread


/**
 * Created by Wilberforce on 2019-10-12 at 03:34.\
 *
 * Base repository for repositories that fetch data from the [MediaStore]
 */
abstract class BaseMediaStoreRepository(private val application: Application) {

    @WorkerThread
    fun<T> loadData(
        uri: Uri,
        projection: Array<String>? = null,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = null,
        transform: (cursor: Cursor) -> T
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

}