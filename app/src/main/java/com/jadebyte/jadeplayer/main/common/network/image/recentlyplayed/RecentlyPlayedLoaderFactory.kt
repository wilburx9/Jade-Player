// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image.recentlyplayed

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.jadebyte.jadeplayer.main.common.network.image.BaseModelLoaderFactory
import com.jadebyte.jadeplayer.main.explore.RecentlyPlayed
import java.io.InputStream


/**
 * Created by Wilberforce on 2019-09-15 at 16:13.
 */
class RecentlyPlayedLoaderFactory : BaseModelLoaderFactory<RecentlyPlayed>() {
    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<RecentlyPlayed, InputStream> {
        return RecentlyPlayedLoader(multiFactory.build(GlideUrl::class.java, InputStream::class.java))
    }
}