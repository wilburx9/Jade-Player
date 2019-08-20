// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.data

/**
 * Created by Wilberforce on 2019-04-20 at 21:30.
 */
object Constants {
    // CloudKeys
    const val acrHost = "com.jadebyte.jadeplayer.main.common.data.acrHost"
    const val acrKey = "com.jadebyte.jadeplayer.main.common.data.acrKey"
    const val acrKeyFile = "com.jadebyte.jadeplayer.main.common.data.acrKeyFile"
    const val acrSecret = "com.jadebyte.jadeplayer.main.common.data.acrSecret"
    const val acrSecretFile = "com.jadebyte.jadeplayer.main.common.data.acrSecretFile"
    const val spotifySecret = "com.jadebyte.jadeplayer.main.common.data.spotifySecret"
    const val lastFmKey = "com.jadebyte.jadeplayer.main.common.data.lastFmKey"
    const val spotifyClientId = "com.jadebyte.jadeplayer.main.common.data.spotifyClientId"


    // Keys for items in NavigationDialogFragment
    const val NAV_SONGS = "com.jadebyte.jadeplayer.nav.songs"
    const val NAV_IDENTIFY = "com.jadebyte.jadeplayer.nav.identify"
    const val NAV_ARTISTS = "com.jadebyte.jadeplayer.nav.artists"
    const val NAV_FAVOURITES = "com.jadebyte.jadeplayer.nav.favourites"
    const val NAV_GENRES = "com.jadebyte.jadeplayer.nav.genres"
    const val NAV_PLAYLIST = "com.jadebyte.jadeplayer.nav.playlist"
    const val NAV_RADIO = "com.jadebyte.jadeplayer.nav.radio"
    const val NAV_SETTINGS = "com.jadebyte.jadeplayer.nav.settings"
    const val NAV_VIDEOS = "com.jadebyte.jadeplayer.nav.videos"


    // Media playback
    const val MEDIA_SEARCH_SUPPORTED = "android.media.browse.SEARCH_SUPPORTED"
    const val CONTENT_STYLE_BROWSABLE_HINT = "android.media.browse.CONTENT_STYLE_BROWSABLE_HINT"
    const val CONTENT_STYLE_PLAYABLE_HINT = "android.media.browse.CONTENT_STYLE_PLAYABLE_HINT"
    const val CONTENT_STYLE_SUPPORTED = "android.media.browse.CONTENT_STYLE_SUPPORTED"
    const val CONTENT_STYLE_LIST = 1
    const val CONTENT_STYLE_GRID = 2
    const val BROWSABLE_ROOT = "/"
    const val EMPTY_ROOT = "@empty@"
    const val RECOMMENDED_ROOT = "__RECOMMENDED__"
    const val ALBUMS_ROOT = "__ALBUMS__"
    const val METADATA_KEY_FLAGS = "com.jadebyte.jadeplayer.playback.METADATA_KEY_UAMP_FLAGS"
    const val NETWORK_FAILURE = "com.jadebyte.jadeplayer.playback.NETWORK_FAILURE"

    // Other constants
    const val MAX_MODEL_IMAGE_THUMB_WIDTH = 100
    val WHITESPACE_REGEX = "\\s|\\n".toRegex()
    const val IMAGE_URI_ROOT = "android.resource://com.jadebyte.jadeplayer/drawable/"

}