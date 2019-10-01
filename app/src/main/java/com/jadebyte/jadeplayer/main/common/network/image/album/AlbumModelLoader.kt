// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image.album

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.jadebyte.jadeplayer.main.albums.Album
import java.io.InputStream

/**
 * Created by Wilberforce on 19/04/2019 at 20:18.
 */
class AlbumModelLoader(concreteLoader: ModelLoader<GlideUrl, InputStream>) :
    BaseAlbumLoader<Album>(concreteLoader) {

    override fun artist(a: Album): String = a.artist

    override fun album(a: Album): String = a.name
}