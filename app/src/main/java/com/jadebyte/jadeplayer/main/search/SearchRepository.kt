// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.search

import android.app.Application
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.albums.baseAlbumProjection
import com.jadebyte.jadeplayer.main.albums.baseAlbumUri
import com.jadebyte.jadeplayer.main.artists.Artist
import com.jadebyte.jadeplayer.main.artists.baseArtistProjection
import com.jadebyte.jadeplayer.main.artists.baseArtistUri
import com.jadebyte.jadeplayer.main.common.data.BaseMediaStoreRepository
import com.jadebyte.jadeplayer.main.genres.Genre
import com.jadebyte.jadeplayer.main.genres.baseGenreProjection
import com.jadebyte.jadeplayer.main.genres.baseGenreUri
import com.jadebyte.jadeplayer.main.playlist.Playlist
import com.jadebyte.jadeplayer.main.playlist.basePlaylistProjection
import com.jadebyte.jadeplayer.main.playlist.basePlaylistUri
import com.jadebyte.jadeplayer.main.songs.*


/**
 * Created by Wilberforce on 2019-10-12 at 03:09.
 */
class SearchRepository(application: Application) : BaseMediaStoreRepository(application) {

    @WorkerThread
    fun querySongs(query: String, ascend: Boolean): List<Song> {
        val selection = "$basicSongsSelection AND ${MediaStore.Audio.Media.TITLE} LIKE ?"
        val selectionArgs = arrayOf(basicSongsSelectionArg, "%$query%")
        val order = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ${if (ascend) "ASC" else "DESC"}"
        return loadData(baseSongUri, baseSongsProjection, selection, selectionArgs, order, ::Song)
    }

    @WorkerThread
    fun queryAlbums(query: String, ascend: Boolean): List<Album> {
        val selection = "${MediaStore.Audio.Media.ALBUM} LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        val order = "${MediaStore.Audio.Media.ALBUM} COLLATE NOCASE ${if (ascend) "ASC" else "DESC"}"
        return loadData(baseAlbumUri, baseAlbumProjection, selection, selectionArgs, order, ::Album)
    }

    @WorkerThread
    fun queryArtists(query: String, ascend: Boolean): List<Artist> {
        val selection = "${MediaStore.Audio.Media.ARTIST} LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        val order = "${MediaStore.Audio.Media.ARTIST} COLLATE NOCASE ${if (ascend) "ASC" else "DESC"}"
        return loadData(baseArtistUri, baseArtistProjection, selection, selectionArgs, order, ::Artist)
    }

    @WorkerThread
    fun queryGenres(query: String, ascend: Boolean): List<Genre> {
        val selection = "${MediaStore.Audio.Genres.NAME} LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        val order = "${MediaStore.Audio.Genres.NAME} COLLATE NOCASE ${if (ascend) "ASC" else "DESC"}"
        return loadData(baseGenreUri, baseGenreProjection, selection, selectionArgs, order, ::Genre)
    }


    @WorkerThread
    fun queryPlaylists(query: String, ascend: Boolean): List<Playlist> {
        val selection = "${MediaStore.Audio.Playlists.NAME} LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        val order = "${MediaStore.Audio.Playlists.NAME} COLLATE NOCASE ${if (ascend) "ASC" else "DESC"}"
        return loadData(basePlaylistUri, basePlaylistProjection, selection, selectionArgs, order, ::Playlist)
    }

}