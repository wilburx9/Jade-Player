// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.common

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.artists.Artist
import com.jadebyte.jadeplayer.main.common.network.image.album.AlbumModelLoaderFactory
import com.jadebyte.jadeplayer.main.common.network.image.artist.ArtistModelLoaderFactory
import java.io.InputStream

/**
 * Created by Wilberforce on 28/03/2019 at 11:58.
 */

@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.prepend(Album::class.java, InputStream::class.java, AlbumModelLoaderFactory())
        registry.prepend(Artist::class.java, InputStream::class.java, ArtistModelLoaderFactory())

    }
}