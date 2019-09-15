// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image.mediaitemdata

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.jadebyte.jadeplayer.main.common.network.image.ImageUrlLoader
import com.jadebyte.jadeplayer.main.playback.MediaItemData
import java.io.InputStream


/**
 * Created by Wilberforce on 2019-09-15 at 02:18.
 */
class MediaItemDataLoader(concreteLoader: ModelLoader<GlideUrl, InputStream>) :
    ImageUrlLoader<MediaItemData>(concreteLoader) {
    override var lastFmUrlKey: String = "album"
    override var spotifyUrlKey: String = "albums"

    override fun getLastFmParams(model: MediaItemData): Map<String, String>? {
        return mapOf(Pair("method", "album.getinfo"), Pair("artist", model.subtitle), Pair("album", model.description))
    }

    override fun getSpotifyFmParams(model: MediaItemData): Map<String, String>? {
        return mapOf(
            Pair("q", String.format("album:%s artist:%s", model.description, model.subtitle)),
            Pair("type", "album")
        )
    }
}