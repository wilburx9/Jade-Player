// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image

import android.app.Application
import android.net.Uri
import com.jadebyte.jadeplayer.main.common.data.CloudKeys
import com.jadebyte.jadeplayer.main.common.network.Connectivity
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

class ImageUrlFetcher(
    val application: Application,
    private val okHttpClient: OkHttpClient,
    private val cacheControl: CacheControl,
    private val cloudKeys: CloudKeys
) {

    fun fetchLastFmUrl(key: String, params: Map<String, String>?): String? {
        if (cloudKeys.lastFmKey.isNullOrEmpty()) {
            return null
        }

        return params?.let {
            val builder = Uri.Builder()
            it.forEach { map ->
                builder.appendQueryParameter(map.key, map.value)
            }
            val uri = builder
                .scheme("https")
                .authority("ws.audioscrobbler.com")
                .appendPath("2.0")
                .appendQueryParameter("method", "album.getinfo")
                .appendQueryParameter("api_key", cloudKeys.lastFmKey)
                .appendQueryParameter("format", "json")
                .build()

            getResponse(uri.toString())?.use { response ->
                if (response.isSuccessful) {
                    val jsonObject = JSONObject(response.body?.string())

                    if (!jsonObject.has(key)) {
                        return null
                    }

                    val array = jsonObject.getJSONObject(key).getJSONArray("image")
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
    }

    fun fetchSpotifyUrl(key: String, params: Map<String, String>?): String? {
        return params?.let {
            val builder = Uri.Builder()
            it.forEach { map ->
                builder.appendQueryParameter(map.key, map.value)
            }
            val uri = builder
                .scheme("https")
                .authority("api.spotify.com")
                .appendPath("v1")
                .appendPath("search")
                .build()



            getResponse(uri.toString())?.use { response ->
                return if (response.isSuccessful) {
                    val jsonObject = JSONObject(response.body?.string())

                    val items = jsonObject.getJSONObject(key).getJSONArray("items")
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
                    return null

                } else null
            }
        }
    }

    fun getResponse(url: String): Response? {
        if (!Connectivity.isConnected(application)) {
            return null
        }
        val request = Request.Builder()
            .url(url)
            .cacheControl(cacheControl)
            .build()

        return okHttpClient.newCall(request).execute()
    }

}