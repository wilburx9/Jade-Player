// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist

import android.app.Application
import android.database.Cursor
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.jadebyte.jadeplayer.main.common.data.MediaStoreRepository

/**
 * Created by Wilberforce on 2019-05-19 at 10:24.
 */
class PlaylistRepository(application: Application) : MediaStoreRepository<Playlist>(application) {

    override fun transform(cursor: Cursor): Playlist = Playlist(cursor)

    @WorkerThread
    fun fetchSongCount(playlistId: Long): Int {
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        val projection = arrayOf(MediaStore.Audio.Playlists.Members.AUDIO_ID)
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != ?"
        val selectionArgs = arrayOf("0")
        val cursor = query(uri, projection, selection, selectionArgs)
        return cursor?.count ?: 0
    }
}