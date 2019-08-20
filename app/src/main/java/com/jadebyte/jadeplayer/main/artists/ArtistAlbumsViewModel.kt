// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.artists

import android.app.Application
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.albums.AlbumsViewModel

/**
 * Created by Wilberforce on 2019-04-25 at 10:17.
 */
class ArtistAlbumsViewModel(application: Application) : AlbumsViewModel(application) {

    override fun init(vararg params: Any?) {
        uri = MediaStore.Audio.Artists.Albums.getContentUri("external", params[0] as Long)
        super.init()
    }

}