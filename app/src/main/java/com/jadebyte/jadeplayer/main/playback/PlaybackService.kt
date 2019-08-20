// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.jadebyte.jadeplayer.main.common.data.Constants

class PlaybackService : MediaBrowserServiceCompat() {
    private lateinit var packageValidator: PackageValidator
    private lateinit var mediaSource: MusicSource
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaController: MediaControllerCompat

    override fun onLoadChildren(parentId: String, result: Result<List<MediaItem>>) {
        // If the media source is ready, the results will be set synchronously here.
        val resultsSent = mediaSource.whenReady { initialized ->
            if (initialized) {
                val children = browseTree[parentId]?.map {
                    MediaItem(it.description, it.flag)
                }
                result.sendResult(children)
            } else {
                mediaSession.sendSessionEvent(Constants.NETWORK_FAILURE, null)
                result.sendResult(null)
            }
        }

        // If the results are not ready, the service must "detach" the results before
        // the method returns. After the source is ready, the lambda above will run,
        // and the caller will be notified that the results are ready.
        if (!resultsSent) {
            result.detach()
        }

    }

    // Return  the "root" media ID that the client should request to get the list of [MediaItem]s to browse play
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        // By default, all known clients are permitted to search, but only tell unknown callers about search if
        // permitted by the BrowserTree

        val isKnownCaller = packageValidator.isKnownCaller(clientPackageName, clientUid)
        val rootExtras = Bundle().apply {
            putBoolean(Constants.MEDIA_SEARCH_SUPPORTED, isKnownCaller || browseTree.searchableByUnknownCaller)
            putBoolean(Constants.CONTENT_STYLE_SUPPORTED, true)
            putInt(Constants.CONTENT_STYLE_BROWSABLE_HINT, Constants.CONTENT_STYLE_GRID)
            putInt(Constants.CONTENT_STYLE_PLAYABLE_HINT, Constants.CONTENT_STYLE_LIST)
        }

        return if (isKnownCaller) {
            // The caller is allowed to browse, so return the root
            BrowserRoot(Constants.BROWSABLE_ROOT, rootExtras)
        } else {
            // Unknown caller. Return a root without any content so the system doesn't disconnect the app
            BrowserRoot(Constants.EMPTY_ROOT, rootExtras)
        }
    }


    private val browseTree: BrowseTree by lazy {
        BrowseTree(applicationContext, mediaSource)
    }
}
