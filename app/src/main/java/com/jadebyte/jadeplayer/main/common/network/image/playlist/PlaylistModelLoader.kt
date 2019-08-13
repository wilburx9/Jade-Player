// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image.playlist

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.StrictMode
import android.provider.MediaStore
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import com.jadebyte.jadeplayer.common.App
import com.jadebyte.jadeplayer.common.dp
import com.jadebyte.jadeplayer.common.inputStream
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
 *
 *  Model loader for loading playlist image.
 *
 *  For playlist that has a saved image file (ie the user selected a file for the playlist),
 *  the file's InputStream is passed over to Glide.
 *
 *  For playlist without a saved image file, the following steps are taken:
 *  1. A distinct list of all albums in the playlist is retrieved with ContentResolver
 *  2. If the Glide target width is greater than 100dp, we retrieve the album art of the first four successful album
 *  arts and then use [ImageUtils.merge] to merge the bitmaps. However, if the Glide target width is less than or equals
 *  to 100dp, we load the album art of the first successful album art.
 *  3. Finally, the InputStream from any of the results in step 2 is passed to Glide
 */
class PlaylistModelLoader :
    ModelLoader<Playlist, InputStream> {

    override fun buildLoadData(
        model: Playlist,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(ObjectKey(model), PlaylistDataFetcher(model, width, height))
    }

    override fun handles(model: Playlist): Boolean = true


    inner class PlaylistDataFetcher(val playlist: Playlist, val width: Int, val height: Int) :
        DataFetcher<InputStream> {

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
            return if (imageFile.exists() || width.dp > 100) DataSource.LOCAL else DataSource.REMOTE
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
            return if (width.dp > 100) {
                fetchMergedBitmapInputStream()
            } else {
                fetchBitmapInputStream()
            }
        }


        private fun fetchBitmapInputStream(): InputStream? {
            val albums = getAlbumsInPlaylist()
            for (album in albums) {
                val imageUrl = getImageUrl(album)
                if (imageUrl.isNullOrEmpty()) continue
                val stream = fetchImageUrlInputStream(imageUrl)
                if (stream != null) return stream
            }
            return null

        }

        private fun fetchMergedBitmapInputStream(): InputStream? {
            val distinctAlbums = getAlbumsInPlaylist()
            if (distinctAlbums.isEmpty()) return null
            val bitmaps = mutableListOf<Bitmap>()
            for (album in distinctAlbums) {
                val imageUrl = getImageUrl(album)
                if (!imageUrl.isNullOrEmpty()) {
                    val inputStream = fetchImageUrlInputStream(imageUrl)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    bitmaps.add(bitmap)
                    // We need four bitmaps at most
                    // We also don't want to load a third bitmap if we think the albums are not up to four(since the
                    // third one will be discarded in this scenario)
                    if (bitmaps.size >= 4 || (bitmaps.size > 1 && distinctAlbums.size < 4)) break

                }
            }

            if (bitmaps.isEmpty()) return null

            val mergedBitmap = ImageUtils.merge(bitmaps, width.toFloat(), height.toFloat())
            return mergedBitmap.inputStream()
        }

        private fun fetchImageUrlInputStream(url: String): InputStream? {
            response = imageUrlFetcher.getResponse(url)
            return response?.let {
                if (it.isSuccessful) it.body?.byteStream() else null
            }
        }

        private fun getImageUrl(album: Album): String? {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().permitNetwork().build())
            var params = mapOf(Pair("method", "album.getinfo"), Pair("artist", album.artist), Pair("album", album.name))
            val url = imageUrlFetcher.fetchLastFmUrl("album", params)
            if (!url.isNullOrEmpty()) return url

            params =
                mapOf(Pair("q", String.format("album:%s artist:%s", album.name, album.artist)), Pair("type", "album"))
            return imageUrlFetcher.fetchSpotifyUrl("albums", params)
        }

        private fun getAlbumsInPlaylist(): List<Album> {
            val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.id)
            val cursor = application.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)

            val results = mutableListOf<Album>()
            cursor?.use {
                while (it.moveToNext()) {
                    results.add(
                        Album(
                            it,
                            it.getLong(it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                        )
                    )
                }
            }
            return if (results.size > 1) results.distinctBy { it.id } else results

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
private const val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} COLLATE NOCASE ASC"