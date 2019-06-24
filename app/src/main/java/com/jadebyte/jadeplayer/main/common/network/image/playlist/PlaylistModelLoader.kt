// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image.playlist

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.jadebyte.jadeplayer.main.playlist.Playlist
import java.io.InputStream

/**
 * Created by Wilberforce on 2019-06-07 at 04:35.
 */
class PlaylistModelLoader: ModelLoader<Playlist, InputStream> {
    override fun buildLoadData(
        model: Playlist,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handles(model: Playlist): Boolean = true
}