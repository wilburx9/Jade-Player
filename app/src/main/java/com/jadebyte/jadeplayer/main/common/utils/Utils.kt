// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.utils

import android.provider.MediaStore


/**
 * Created by Wilberforce on 2019-04-21 at 23:47.
 */
object Utils {

    /**
     * Removes the the first three character oif [MediaStore.Audio.Media.TRACK]
     */
    fun getTrackNumber(number: Int): String {
        val num = number.toString()
        return if (num.length >= 4) num.drop(3) else num
    }


}