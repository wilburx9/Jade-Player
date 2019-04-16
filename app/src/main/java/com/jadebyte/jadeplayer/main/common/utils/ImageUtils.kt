// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.utils

import android.content.ContentUris
import android.net.Uri


/**
 * Created by Wilberforce on 16/04/2019 at 01:31.
 */
object ImageUtils {

    // get the album cover URi from the album Id
    fun getAlbumArtUri(albumId: Long): Uri {
        val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(sArtworkUri, albumId)
    }
}