// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.utils

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
}