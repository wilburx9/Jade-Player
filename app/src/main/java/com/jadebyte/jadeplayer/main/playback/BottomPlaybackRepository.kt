// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback

import android.app.Application
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.jadebyte.jadeplayer.main.songs.Song

/**
 * Created by Wilberforce on 2019-05-01 at 18:49.
 */
class BottomPlaybackRepository(val application: Application) {

    // For now, we are getting a random song. Later, we'll get the first song from the database of recently played songs
    // or the first song from the list of currently playing songs
    @WorkerThread
    fun loadData(): Song {
        val cursor = application.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            "${MediaStore.Audio.Media.IS_MUSIC} != ?",
            arrayOf("0"),
            "RANDOM() LIMIT 1"
        )
        var song: Song? = null
        cursor?.use {
            it.moveToFirst()
            song = Song(it)
        }
        return song!!
    }

}