// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.data

import android.text.TextUtils

/**
 * Created by Wilberforce on 19/04/2019 at 23:15.
 * Container for API keys and secrets
 */
data class CloudKeys(
        val acrHost: String? = null,
        val acrKey: String? = null,
        val acrKeyFile: String? = null,
        val acrSecret: String? = null,
        val acrSecretFile: String? = null,
        val spotifySecret: String? = null,
        val lastFmKey: String? = null,
        val spotifyClientId: String? = null
) {
    fun isEmpty(): Boolean =
            TextUtils.isEmpty(acrSecret) && TextUtils.isEmpty(spotifySecret) && TextUtils.isEmpty(lastFmKey)

}