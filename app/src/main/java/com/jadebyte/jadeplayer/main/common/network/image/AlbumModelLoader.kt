// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image

import android.app.Application
import android.net.Uri
import android.os.StrictMode
import android.text.TextUtils
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader
import com.jadebyte.jadeplayer.common.App
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.common.data.CloudKeys
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.InputStream
import javax.inject.Inject

/**
 * Created by Wilberforce on 19/04/2019 at 20:18.
 */
class AlbumModelLoader(concreteLoader: ModelLoader<GlideUrl, InputStream>?) :
    BaseGlideUrlLoader<Album>(concreteLoader) {

    @Inject
    lateinit var application: Application
    @Inject
    lateinit var okHttpClient: OkHttpClient
    @Inject
    lateinit var cloudKeys: CloudKeys

    init {
        App.appComponent.inject(this)
    }

    override fun getUrl(model: Album, width: Int, height: Int, options: Options?): String? {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX)
        return getImageUrl(model)
    }

    override fun handles(model: Album): Boolean {
        return true
    }

    private fun getImageUrl(model: Album): String? {
        val url = getUrlFrmSpotify(model)
        return if (TextUtils.isEmpty(url)) getUrlFrmLastFm(model) else url
    }

    private fun getUrlFrmLastFm(model: Album): String? {
        if (TextUtils.isEmpty(cloudKeys.lastFmKey)) {
            return null
        }
        val lastFmUrl = Uri.Builder()
            .scheme("http")
            .authority("ws.audioscrobbler.com")
            .appendPath("2.0")
            .appendQueryParameter("method", "album.getinfo")
            .appendQueryParameter("api_key", cloudKeys.lastFmKey)
            .appendQueryParameter("artist", model.artist)
            .appendQueryParameter("album", model.name)
            .appendQueryParameter("format", "json")
            .build()

        val request = Request.Builder()
            .url(lastFmUrl.toString())
            .build()

        val response = okHttpClient.newCall(request).execute()

        response.use {
            if (response.isSuccessful) {
                val jsonObject = JSONObject(response.body()?.string())

                if (!jsonObject.has("album")) {
                    return null
                }

                val array = jsonObject.getJSONObject("album").getJSONArray("image")
                for (i in 0 until array.length()) {
                    val image = array.getJSONObject(i)
                    if (image.getString("size") == "extralarge") {
                        return image.getString("#text")
                    }
                }
            }
        }
        return null
    }

    private fun getUrlFrmSpotify(model: Album): String? {
        val spotifyUrl = Uri.Builder()
            .scheme("https")
            .authority("api.spotify.com")
            .appendPath("v1")
            .appendPath("search")
            .appendQueryParameter("q", String.format("album:%s artist:%s", model.name, model.artist))
            .appendQueryParameter("type", "album")
            .build()


        val request = Request.Builder()
            .url(spotifyUrl.toString())
            .build()

        val response = okHttpClient.newCall(request).execute()

        response.use {
            if (response.isSuccessful) {
                val jsonObject = JSONObject(response.body()?.string())

                val items = jsonObject.getJSONObject("albums").getJSONArray("items")
                if (items.isNull(0)) {
                    return null
                }
                val array = items.getJSONObject(0).getJSONArray("images")

                for (i in 0 until array.length()) {
                    val image = array.getJSONObject(i)
                    val height = image.getInt("height")
                    if (height > 350) {
                        return image.getString("url")
                    }
                }

                if (!array.isNull(0)) {
                    return array.getJSONObject(0).getString("url")
                }
            }
        }
        return null
    }

}