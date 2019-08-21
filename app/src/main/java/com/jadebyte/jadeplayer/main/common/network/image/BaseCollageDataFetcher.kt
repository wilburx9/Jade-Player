// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.StrictMode
import android.provider.MediaStore
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.jadebyte.jadeplayer.common.dp
import com.jadebyte.jadeplayer.common.inputStream
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.common.data.Constants
import com.jadebyte.jadeplayer.main.common.data.Model
import com.jadebyte.jadeplayer.main.common.utils.ImageUtils
import okhttp3.Response
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.io.IOException
import java.io.InputStream


/**
 *  Created by Wilberforce on 2019-08-17 at 23:24.
 *
 *  A Glide Model loader for generating collage from album arts.
 *
 *  For a [Model] that has a saved image file (ie the user selected a file for the [Model]
 *  e.g when creating a playlist), the file's InputStream is passed over to Glide.
 *
 *  For [Model] without a saved image file, the following steps are taken:
 *  1. A distinct list of all albums in the [Model] is retrieved with ContentResolver
 *  2. If the Glide target width is greater than [Constants.MAX_MODEL_IMAGE_THUMB_WIDTH]dp,
 *  we retrieve the album art of the first four successful album
 *  arts and then generate a collage with the bitmaps. However, if the Glide target width is less than or equals
 *  to  [Constants.MAX_MODEL_IMAGE_THUMB_WIDTH]dp, we load the album art of the first successful album art.
 *  3. Finally, the InputStream from any of the results in step 2 is passed to Glide
 *
 *  @param model the model to load
 *  @param width width of the Glide target in pixels
 *  @param height height of the Glide target in pixels
 *  @param useFile whether to consider loading a local saved file if available. Pass true to consider loading a file
 *  and false to skip local file loading altogether
 */
abstract class BaseCollageDataFetcher(
    val model: Model,
    val width: Int,
    val height: Int,
    private val useFile: Boolean
) :
    DataFetcher<InputStream>, KoinComponent {

    private val imageUrlFetcher: ImageUrlFetcher by inject()
    val application: Application by inject()
    abstract var imageFile: File
    private var inputStream: InputStream? = null
    private var response: Response? = null
    private var cancelled = false
    abstract var modelMemberMediaUri: Uri

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    override fun cleanup() {
        inputStream?.close()
        response?.body?.close()
    }

    override fun getDataSource(): DataSource = if (useFile()) DataSource.LOCAL else DataSource.REMOTE

    override fun cancel() {
        cancelled = true
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        if (!cancelled) {
            inputStream = if (useFile()) fetchFileInputStream() else fetchCollageInputStream()
            callback.onDataReady(inputStream)
        } else {
            callback.onLoadFailed(IOException("Forced Glide network failure. Can't load image for ${model.javaClass.name}"))
        }
    }

    private fun fetchFileInputStream(): InputStream = imageFile.inputStream()

    private fun fetchCollageInputStream(): InputStream? {
        if (!hasValidData()) return null
        return if (width.dp > Constants.MAX_MODEL_IMAGE_THUMB_WIDTH) {
            fetchMergedBitmapInputStream()
        } else {
            fetchBitmapInputStream()
        }
    }

    private fun fetchBitmapInputStream(): InputStream? {
        val albums = getAlbumsInModel()
        for (album in albums) {
            val imageUrl = getImageUrl(album)
            if (imageUrl.isNullOrEmpty()) continue
            val stream = fetchImageUrlInputStream(imageUrl)
            if (stream != null) return stream
        }
        return null

    }

    private fun fetchMergedBitmapInputStream(): InputStream? {
        val distinctAlbums = getAlbumsInModel()
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

    private fun getAlbumsInModel(): List<Album> {
        val cursor =
            application.contentResolver.query(modelMemberMediaUri, projection, selection, selectionArgs, sortOrder)

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

    private fun useFile(): Boolean {
        if (useFile && imageFile.exists()) return true
        return false
    }

    open fun hasValidData(): Boolean = true
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