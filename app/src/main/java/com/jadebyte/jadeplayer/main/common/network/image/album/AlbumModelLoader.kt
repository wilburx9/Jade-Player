// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image.album

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.jadebyte.jadeplayer.common.App
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.common.network.image.BaseModelLoader
import java.io.InputStream

/**
 * Created by Wilberforce on 19/04/2019 at 20:18.
 */
class AlbumModelLoader(concreteLoader: ModelLoader<GlideUrl, InputStream>) : BaseModelLoader<Album>(concreteLoader) {
    override var lastFmUrlKey: String = "album"
    override var spotifyUrlKey: String = "albums"

    init {
        App.appComponent.inject(this)
    }

    override fun getLastFmParams(model: Album): Map<String, String> {
        return mapOf(Pair("method", "album.getinfo"), Pair("artist", model.artist), Pair("album", model.name))
    }

    override fun getSpotifyFmParams(model: Album): Map<String, String> {
        return mapOf(Pair("q",  String.format("album:%s artist:%s", model.name, model.artist)), Pair("type", "album"))
    }


}