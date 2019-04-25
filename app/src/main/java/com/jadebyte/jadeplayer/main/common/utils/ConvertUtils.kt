// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.utils

import android.content.res.Resources
import android.util.Base64
import java.io.UnsupportedEncodingException


/**
 * Created by Wilberforce on 19/04/2019 at 22:35.
 */
object ConvertUtils {
    /**
     * Convert string to base64-encoded string
     */
    fun stringToBase64(string: String): String {
        return try {
            Base64.encodeToString(string.toByteArray(), Base64.NO_WRAP)
        } catch (e: UnsupportedEncodingException) {
            ""
        }
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    /**
     * This method converts px unit to equivalent dp, depending on device density.
     *
     * @param px A value in pixels unit. Which we need to convert into dp
     * @return A float value to represent dp equivalent to px depending on device density
     */
    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }
}