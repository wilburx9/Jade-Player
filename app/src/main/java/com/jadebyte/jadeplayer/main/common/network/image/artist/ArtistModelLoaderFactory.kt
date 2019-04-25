// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image.artist

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.artists.Artist
import com.jadebyte.jadeplayer.main.common.network.image.BaseModelLoaderFactory
import com.jadebyte.jadeplayer.main.common.network.image.album.AlbumModelLoader
import java.io.InputStream

/**
 * Created by Wilberforce on 2019-04-25 at 06:47.
 */
class ArtistModelLoaderFactory: BaseModelLoaderFactory<Artist>(){
    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<Artist, InputStream> {
        return ArtistModelLoader(multiFactory.build(GlideUrl::class.java, InputStream::class.java))
    }

}