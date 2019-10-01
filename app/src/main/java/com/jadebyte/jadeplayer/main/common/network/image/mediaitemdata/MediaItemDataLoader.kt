// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image.mediaitemdata

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.jadebyte.jadeplayer.main.common.network.image.album.BaseAlbumLoader
import com.jadebyte.jadeplayer.main.playback.MediaItemData
import java.io.InputStream


/**
 * Created by Wilberforce on 2019-09-15 at 02:18.
 */
class MediaItemDataLoader(concreteLoader: ModelLoader<GlideUrl, InputStream>) :
    BaseAlbumLoader<MediaItemData>(concreteLoader) {

    override fun artist(a: MediaItemData): String = a.subtitle

    override fun album(a: MediaItemData): String = a.description
}