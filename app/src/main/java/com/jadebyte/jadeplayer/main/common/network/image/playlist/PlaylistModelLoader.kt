// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image.playlist

import android.provider.MediaStore
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.jadebyte.jadeplayer.common.App
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.common.network.image.BaseModelLoader
import com.jadebyte.jadeplayer.main.playlist.Playlist
import java.io.InputStream

/**
 * Created by Wilberforce on 2019-06-07 at 04:35.
 */
class PlaylistModelLoader(concreteLoader: ModelLoader<GlideUrl, InputStream>) :
    BaseModelLoader<Playlist>(concreteLoader) {
    override var lastFmUrlKey: String = "album"
    override var spotifyUrlKey: String = "albums"

    init {
        App.appComponent.inject(this)
    }

    override fun getLastFmParams(model: Playlist): Map<String, String>? {
        val album = getAlbumOfFirstSong(model.id) ?: return null
        return mapOf(
            Pair("method", "album.getinfo"),
            Pair("artist", album.artist),
            Pair("album", album.name)
        )
    }

    override fun getSpotifyFmParams(model: Playlist): Map<String, String>? {
        val album = getAlbumOfFirstSong(model.id) ?: return null
        return mapOf(
            Pair("q", String.format("album:%s artist:%s", album.name, album.artist)),
            Pair("type", "album")
        )
    }

    private fun getAlbumOfFirstSong(playlistId: Long): Album? {
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        val cursor = application.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)

        return cursor?.use {
            if (it.moveToFirst()) Album(it, it.getLong(it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))) else null
        }

    }

}

private val projection = arrayOf(
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.ALBUM,
    MediaStore.Audio.Media.ALBUM_ID
)
private const val selection = "${MediaStore.Audio.Media.IS_MUSIC} != ?"
private val selectionArgs = arrayOf("0")
private const val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} COLLATE NOCASE ASC LIMIT 1"