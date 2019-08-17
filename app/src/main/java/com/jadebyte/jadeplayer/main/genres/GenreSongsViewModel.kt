// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.genres

import android.app.Application
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.songs.SongsViewModel

class GenreSongsViewModel(application: Application): SongsViewModel(application) {

    fun init(genreId: Long) {
        uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId)
        init()
    }
}