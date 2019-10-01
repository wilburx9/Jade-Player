// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image

import android.os.StrictMode
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.InputStream

abstract class ImageUrlLoader<M>(concreteLoader: ModelLoader<GlideUrl, InputStream>?) :
    BaseGlideUrlLoader<M>(concreteLoader), KoinComponent {

    private val imageUrlFetcher: ImageUrlFetcher by inject()

    abstract var lastFmUrlKey: String
    abstract var spotifyUrlKey: String

    final override fun getUrl(model: M, width: Int, height: Int, options: Options?): String? {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().permitNetwork().build())

        val url = imageUrlFetcher.fetchLastFmUrl(lastFmUrlKey, getLastFmParams(model))

        return if (url.isNullOrEmpty())
            imageUrlFetcher.fetchSpotifyUrl(spotifyUrlKey, getSpotifyFmParams(model)
        ) else url
    }

    final override fun handles(model: M): Boolean = true

    abstract fun getLastFmParams(model: M): Map<String, String>?

    abstract fun getSpotifyFmParams(model: M): Map<String, String>?

}