// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.data

import android.content.SharedPreferences
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
    fun isValid(): Boolean =
        TextUtils.isEmpty(acrSecret) && TextUtils.isEmpty(spotifySecret) && TextUtils.isEmpty(lastFmKey) &&
                TextUtils.isEmpty(spotifyClientId) && TextUtils.isEmpty(acrHost) && TextUtils.isEmpty(acrKey) &&
                TextUtils.isEmpty(acrKeyFile) && TextUtils.isEmpty(acrSecretFile)

    constructor(pref: SharedPreferences) : this(
        acrHost = pref.getString(Constants.acrHost, null),
        acrKey = pref.getString(Constants.acrKey, null),
        acrKeyFile = pref.getString(Constants.acrKeyFile, null),
        acrSecret = pref.getString(Constants.acrSecret, null),
        acrSecretFile = pref.getString(Constants.acrSecretFile, null),
        lastFmKey = pref.getString(Constants.lastFmKey, null),
        spotifyClientId = pref.getString(Constants.spotifyClientId, null),
        spotifySecret = pref.getString(Constants.spotifySecret, null)
    )
}