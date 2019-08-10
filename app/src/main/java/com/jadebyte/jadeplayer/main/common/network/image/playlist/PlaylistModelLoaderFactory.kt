// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image.playlist

import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.jadebyte.jadeplayer.main.playlist.Playlist
import java.io.InputStream

/**
 * Created by Wilberforce on 2019-06-07 at 04:39.
 */
class PlaylistModelLoaderFactory : ModelLoaderFactory<Playlist, InputStream> {
    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<Playlist, InputStream> {
        return PlaylistModelLoader()
    }

    override fun teardown() {}

}