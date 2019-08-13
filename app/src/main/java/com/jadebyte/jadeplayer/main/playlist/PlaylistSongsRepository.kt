// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist

import android.app.Application
import android.database.Cursor
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.songs.Song
import com.jadebyte.jadeplayer.main.songs.SongsRepository

class PlaylistSongsRepository(application: Application) : SongsRepository(application) {

    override fun transform(cursor: Cursor): Song {
        return Song(cursor, cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)))
    }
}