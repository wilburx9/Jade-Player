// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.upstream.DataSource
import com.jadebyte.jadeplayer.main.common.data.Constants
import timber.log.Timber


/**
 * Created by Wilberforce on 2019-08-25 at 12:44.
 */
class PlaybackPreparer(
    private val musicSource: MusicSource,
    private val exoPlayer: ExoPlayer,
    private val dataSourceFactory: DataSource.Factory,
    private val preferences: SharedPreferences
    ) : MediaSessionConnector.PlaybackPreparer {

    /**
     *  Handles callbacks to both [MediaSessionCompat.Callback.onPrepareFromSearch] *AND*
     *  [MediaSessionCompat.Callback.onPlayFromSearch] when using [MediaSessionConnector].
     *  (See above for details)
     *
     *  This method is used by the Google Assistant to respond to requests such as
     *  - Play Geisha from Wake Up on Jade Player
     *  - Play electronic music on Jade Player
     *  - Play music on Jade Player
     */
    override fun onPrepareFromSearch(query: String?, playWhenReady: Boolean, extras: Bundle?) {
        musicSource.whenReady {
            val metadataList = musicSource.search(query ?: "", extras ?: Bundle.EMPTY)
            if (metadataList.isNotEmpty()) {
                val mediaSource = metadataList.toMediaSource(dataSourceFactory)
                exoPlayer.prepare(mediaSource)
            }
        }
    }

    override fun onCommand(
        player: Player?,
        controlDispatcher: ControlDispatcher?,
        command: String?,
        extras: Bundle?,
        cb: ResultReceiver?
    ) = false

    // We are only supporting preparing and playing from search and media id
    // TODO: Add support for ACTION_PREPARE and ACTION_PLAY, which mean "prepare/play something".
    override fun getSupportedPrepareActions() =
        PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH

    /**
     *  Handles callbacks to both [MediaSessionCompat.Callback.onPrepareFromMediaId] *AND*
     *  [MediaSessionCompat.Callback.onPlayFromMediaId] when using [MediaSessionConnector].
     *  This is done with expectation that "play" is just "prepare" + "play".
     */
    override fun onPrepareFromMediaId(mediaId: String?, playWhenReady: Boolean, extras: Bundle?) {
        musicSource.whenReady { _ ->
            val itemToPlay = musicSource.find { it.id == mediaId }
            if (itemToPlay == null) {
                Timber.w("onPrepareFromMediaId: Song with id $mediaId not found")
                return@whenReady
            }

            val metadataList = musicSource.toList()
            val mediaSource = metadataList.toMediaSource(dataSourceFactory)

            val positionMs = if (itemToPlay.id == preferences.getString(Constants.LAST_ID, null)) {
                preferences.getLong(Constants.LAST_POSITION, 0)
            } else {
                0
            }

            // Since the playlist was probably based on some ordering (such as tracks
            // on an album), find which window index to play first so that the song the
            // user actually wants to hear plays first.
            val initialWindowIndex = metadataList.indexOf(itemToPlay)
            exoPlayer.playWhenReady = playWhenReady
            exoPlayer.prepare(mediaSource)
            exoPlayer.seekTo(initialWindowIndex, positionMs)
        }
    }

    /**
     * Builds a playlist based on a [MediaMetadataCompat]
     * TODO: Support building a playlist by artist, genre, etc
     * @param itemToPlay Item to base the playlist on
     * @return a [List] of [MediaMetadataCompat] objects representing a playlist
     */
    private fun buildPlaylist(itemToPlay: MediaMetadataCompat): List<MediaMetadataCompat> =
        musicSource.filter { it.album == itemToPlay.album }.sortedBy { it.trackNumber }


    override fun onPrepareFromUri(uri: Uri?, playWhenReady: Boolean, extras: Bundle?) = Unit

    override fun onPrepare(playWhenReady: Boolean) = Unit
}