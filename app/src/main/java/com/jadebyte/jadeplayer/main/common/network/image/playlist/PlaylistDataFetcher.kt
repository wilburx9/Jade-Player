// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image.playlist

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.jadebyte.jadeplayer.main.playlist.Playlist
import okhttp3.Response
import java.io.InputStream


/**
 * Created by Wilberforce on 2019-06-07 at 04:46.
 */
class PlaylistDataFetcher(private val playlist: Playlist): DataFetcher<InputStream> {
    private var isCancelled = false
    private val size: Int = 0
    private val inputStream: InputStream? = null
    private val response: Response? = null

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    override fun cleanup() {
        inputStream?.close()
        response?.body()?.close()
    }

    override fun getDataSource(): DataSource = DataSource.REMOTE

    override fun cancel() {
        isCancelled = true
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}