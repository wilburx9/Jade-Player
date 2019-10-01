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
import com.jadebyte.jadeplayer.main.common.network.image.genre.GenreModelLoaderFactory
import com.jadebyte.jadeplayer.main.common.network.image.mediaitemdata.MediaItemDataLoaderFactory
import com.jadebyte.jadeplayer.main.common.network.image.playlist.PlaylistModelLoaderFactory
import com.jadebyte.jadeplayer.main.common.network.image.recentlyplayed.RecentlyPlayedLoaderFactory
import com.jadebyte.jadeplayer.main.explore.RecentlyPlayed
import com.jadebyte.jadeplayer.main.genres.Genre
import com.jadebyte.jadeplayer.main.playback.MediaItemData
import com.jadebyte.jadeplayer.main.playlist.Playlist
import java.io.InputStream

/**
 * Created by Wilberforce on 28/03/2019 at 11:58.
 */

@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.prepend(Album::class.java, InputStream::class.java, AlbumModelLoaderFactory())
        registry.prepend(Artist::class.java, InputStream::class.java, ArtistModelLoaderFactory())
        registry.prepend(Playlist::class.java, InputStream::class.java, PlaylistModelLoaderFactory())
        registry.prepend(Genre::class.java, InputStream::class.java, GenreModelLoaderFactory())
        registry.prepend(MediaItemData::class.java, InputStream::class.java, MediaItemDataLoaderFactory())
        registry.prepend(RecentlyPlayed::class.java, InputStream::class.java, RecentlyPlayedLoaderFactory())
    }
}