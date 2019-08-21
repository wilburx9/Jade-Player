// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image.artist

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.jadebyte.jadeplayer.common.App
import com.jadebyte.jadeplayer.main.artists.Artist
import com.jadebyte.jadeplayer.main.common.network.image.ImageUrlLoader
import java.io.InputStream

/**
 * Created by Wilberforce on 2019-04-25 at 06:48.
 */
class ArtistModelLoader(concreteLoader: ModelLoader<GlideUrl, InputStream>) : ImageUrlLoader<Artist>(concreteLoader) {
    override var lastFmUrlKey: String = "artist"
    override var spotifyUrlKey: String = "artists"

    override fun getLastFmParams(model: Artist): Map<String, String>? {
        return mapOf(Pair("method", "artist.getinfo"), Pair("artist", model.name))
    }

    override fun getSpotifyFmParams(model: Artist): Map<String, String>? {
        return mapOf(Pair("q", String.format("artist:%s", model.name)), Pair("type", "artist"))
    }


}