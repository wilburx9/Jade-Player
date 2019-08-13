// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image.playlist

import android.app.Application
import android.os.StrictMode
import android.provider.MediaStore
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import com.jadebyte.jadeplayer.common.App
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.common.network.image.ImageUrlFetcher
import com.jadebyte.jadeplayer.main.common.utils.ImageUtils
import com.jadebyte.jadeplayer.main.playlist.Playlist
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

/**
 * Created by Wilberforce on 2019-06-07 at 04:35.
 */
class PlaylistModelLoader :
    ModelLoader<Playlist, InputStream> {

    override fun buildLoadData(
        model: Playlist,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(ObjectKey(model), PlaylistDataFetcher(model))
    }

    override fun handles(model: Playlist): Boolean = true


    inner class PlaylistDataFetcher(val playlist: Playlist) : DataFetcher<InputStream> {

        @Inject lateinit var imageUrlFetcher: ImageUrlFetcher
        @Inject lateinit var application: Application

        private val imageFile: File


        init {
            App.appComponent.inject(this)
            this.imageFile = File(ImageUtils.getImagePathForPlaylist(playlist.id, application))
        }

        private var inputStream: InputStream? = null
        private var response: Response? = null
        private var cancelled = false

        override fun getDataClass(): Class<InputStream> = InputStream::class.java

        override fun cleanup() {
            inputStream?.close()
            response?.body?.close()
        }

        override fun getDataSource(): DataSource {
            return if (imageFile.exists()) DataSource.LOCAL else DataSource.REMOTE
        }

        override fun cancel() {
            cancelled = true
        }

        override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
            if (!cancelled) {
                inputStream = if (imageFile.exists()) fetchFileInputStream() else fetchUrlInputStream()
                callback.onDataReady(inputStream)
            } else {
                callback.onLoadFailed(IOException("Forced Glide network failure. Can't load Playlist image"))
            }
        }

        private fun fetchFileInputStream(): InputStream = imageFile.inputStream()

        private fun fetchUrlInputStream(): InputStream? {
            val url = getImageUrl() ?: return null
            response = imageUrlFetcher.getResponse(url)
            return response?.let {
                if (it.isSuccessful) it.body?.byteStream() else null
            }
        }

        private fun getImageUrl(): String? {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().permitNetwork().build())
            return getAlbumOfFirstSong()?.let {

                var params = mapOf(Pair("method", "album.getinfo"), Pair("artist", it.artist), Pair("album", it.name))
                val url = imageUrlFetcher.fetchLastFmUrl("album", params)
                if (!url.isNullOrEmpty()) return url

                params =
                    mapOf(Pair("q", String.format("album:%s artist:%s", it.name, it.artist)), Pair("type", "album"))
                return imageUrlFetcher.fetchSpotifyUrl("albums", params)
            }
        }

        private fun getAlbumOfFirstSong(): Album? {
            val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.id)
            val cursor = application.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)

            return cursor?.use {
                if (it.moveToFirst()) Album(
                    it,
                    it.getLong(it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                ) else null
            }

        }


    }

}

private val projection = arrayOf(
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.ALBUM,
    MediaStore.Audio.Media.ALBUM_ID,
    MediaStore.Audio.Media.ALBUM_KEY
)
private const val selection = "${MediaStore.Audio.Media.IS_MUSIC} != ?"
private val selectionArgs = arrayOf("0")
private const val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} COLLATE NOCASE ASC LIMIT 1"