// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image.album

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.common.network.image.BaseModelLoaderFactory
import java.io.InputStream

/**
 * Created by Wilberforce on 2019-04-20 at 13:00.
 */
class AlbumModelLoaderFactory : BaseModelLoaderFactory<Album>() {
    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<Album, InputStream> {
        return AlbumModelLoader(multiFactory.build(GlideUrl::class.java, InputStream::class.java))
    }

}