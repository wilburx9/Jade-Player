// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image.recentlyplayed

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.jadebyte.jadeplayer.main.common.network.image.album.BaseAlbumLoader
import com.jadebyte.jadeplayer.main.explore.RecentlyPlayed
import java.io.InputStream


/**
 * Created by Wilberforce on 2019-09-15 at 15:08.
 */
class RecentlyPlayedLoader(concreteLoader: ModelLoader<GlideUrl, InputStream>) :
    BaseAlbumLoader<RecentlyPlayed>(concreteLoader) {

    override fun artist(a: RecentlyPlayed): String = a.artist

    override fun album(a: RecentlyPlayed): String = a.album
}