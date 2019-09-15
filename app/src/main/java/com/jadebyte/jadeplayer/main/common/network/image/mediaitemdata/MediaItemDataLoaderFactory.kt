// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image.mediaitemdata

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.jadebyte.jadeplayer.main.common.network.image.BaseModelLoaderFactory
import com.jadebyte.jadeplayer.main.playback.MediaItemData
import java.io.InputStream


/**
 * Created by Wilberforce on 2019-09-15 at 02:53.
 */
class MediaItemDataLoaderFactory : BaseModelLoaderFactory<MediaItemData>() {
    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MediaItemData, InputStream> {
        return MediaItemDataLoader(multiFactory.build(GlideUrl::class.java, InputStream::class.java))
    }
}