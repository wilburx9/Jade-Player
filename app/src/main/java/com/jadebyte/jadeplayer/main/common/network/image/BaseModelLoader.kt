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
import com.jadebyte.jadeplayer.main.common.data.CloudKeys
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.InputStream
import javax.inject.Inject

/**
 * Created by Wilberforce on 2019-04-25 at 06:49.
 */
abstract class BaseModelLoader<M>(concreteLoader: ModelLoader<GlideUrl, InputStream>?) : BaseGlideUrlLoader<M>(concreteLoader) {
    @Inject lateinit var application: Application
    @Inject lateinit var okHttpClient: OkHttpClient
    @Inject lateinit var cloudKeys: CloudKeys
    @Inject lateinit var cacheControl: CacheControl


    abstract var lastFmUrlKey: String
    abstract var spotifyUrlKey: String

    final override fun getUrl(model: M, width: Int, height: Int, options: Options?): String? {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().permitNetwork().build())
        return getImageUrl(model)
    }

    final override fun handles(model: M): Boolean {
        return true
    }

    private fun getImageUrl(model: M): String? {
        val url = getUrlFrmSpotify(model)
        return if (TextUtils.isEmpty(url)) getLastFmImageUrl(model) else url
    }


    private fun getUrlFrmSpotify(model: M): String? {
        val builder = Uri.Builder()
        getSpotifyFmParams(model).forEach {
            builder.appendQueryParameter(it.key, it.value)
        }
        val uri = builder
                .scheme("https")
                .authority("api.spotify.com")
                .appendPath("v1")
                .appendPath("search")
                .build()



        getResponse(uri).use {
            if (it.isSuccessful) {
                val jsonObject = JSONObject(it.body()?.string())

                val items = jsonObject.getJSONObject(spotifyUrlKey).getJSONArray("items")
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


    private fun getLastFmImageUrl(model: M): String? {
        if (TextUtils.isEmpty(cloudKeys.lastFmKey)) {
            return null
        }
        val builder = Uri.Builder()
        getLastFmParams(model).forEach {
            builder.appendQueryParameter(it.key, it.value)
        }
        val uri = builder
                .scheme("http")
                .authority("ws.audioscrobbler.com")
                .appendPath("2.0")
                .appendQueryParameter("method", "album.getinfo")
                .appendQueryParameter("api_key", cloudKeys.lastFmKey)
                .appendQueryParameter("format", "json")
                .build()

        getResponse(uri).use {
            if (it.isSuccessful) {
                val jsonObject = JSONObject(it.body()?.string())

                if (!jsonObject.has(lastFmUrlKey)) {
                    return null
                }

                val array = jsonObject.getJSONObject(lastFmUrlKey).getJSONArray("image")
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

    private fun getResponse(uri: Uri): Response {
        val request = Request.Builder()
                .url(uri.toString())
                .cacheControl(cacheControl)
                .build()

        return okHttpClient.newCall(request).execute()
    }

    abstract fun getLastFmParams(model: M): Map<String, String>

    abstract fun getSpotifyFmParams(model: M): Map<String, String>


}