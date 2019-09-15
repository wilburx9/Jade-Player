// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback


import android.net.Uri
import android.os.Parcelable
import android.support.v4.media.MediaBrowserCompat.MediaItem
import com.jadebyte.jadeplayer.main.common.data.Model
import kotlinx.android.parcel.Parcelize


/**
 * Created by Wilberforce on 2019-08-25 at 16:59.
 */
@Parcelize
data class MediaItemData(
    override val id: String,
    val title: String,
    val subtitle: String, // Artist
    val description: String, //Album
    val albumArtUri: Uri?,
    val isBrowsable: Boolean,
    var isPlaying: Boolean,
    var isBuffering: Boolean,
    var duration: Long = 0L
) : Model(), Parcelable {

    constructor(item: MediaItem, isPlaying: Boolean, isBuffering: Boolean) : this(
        id = item.mediaId!!,
        title = item.description.title!!.toString(),
        subtitle = item.description.subtitle!!.toString(),
        albumArtUri = item.description.iconUri,
        description = item.description.description.toString(),
        isBrowsable = item.isBrowsable,
        isPlaying = isPlaying,
        isBuffering = isBuffering
    )
}