// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.
// A little modification was made from the original file: https://github.com/googlesamples/android-UniversalMusicPlayer/raw/master/common/src/main/java/com/example/android/uamp/common/MediaSessionConnection.kt

package com.jadebyte.jadeplayer.main.playback

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import androidx.media.MediaBrowserServiceCompat
import com.jadebyte.jadeplayer.main.common.data.Constants


/**
 * Created by Wilberforce on 2019-08-20 at 22:32.
 *
 * Class that manages a connection to a [MediaBrowserServiceCompat] instance.
 *
 */
class MediaSessionConnection(context: Context, serviceComponent: ComponentName, val preferences: SharedPreferences) {

    private val connectionCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowser =
        MediaBrowserCompat(context, serviceComponent, connectionCallback, null).apply { connect() }
    private lateinit var mediaController: MediaControllerCompat

    val isConnected = MutableLiveData<Boolean>().apply { postValue(false) }
    val networkFailure = MutableLiveData<Boolean>().apply { postValue(false) }
    val rootMediaId get() = mediaBrowser.root

    val playbackState = MutableLiveData<PlaybackStateCompat>().apply { postValue(EMPTY_PLAYBACK_STATE) }
    val nowPlaying = MutableLiveData<MediaMetadataCompat>().apply { postValue(NOTHING_PLAYING) }
    val shuffleMode = MutableLiveData<Int>().apply {
        postValue(preferences.getInt(Constants.LAST_SHUFFLE_MODE, PlaybackStateCompat.SHUFFLE_MODE_NONE))
    }
    val repeatMode = MutableLiveData<Int>().apply {
        postValue(preferences.getInt(Constants.LAST_REPEAT_MODE, PlaybackStateCompat.REPEAT_MODE_NONE))
    }

    val transportControls: MediaControllerCompat.TransportControls get() = mediaController.transportControls


    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
        preferences.edit().putString(Constants.LAST_PARENT_ID, parentId).apply()
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }


    fun sendCommand(command: String, parameters: Bundle?) = sendCommand(command, parameters) { _, _ -> }

    private fun sendCommand(
        command: String, parameters: Bundle?, resultCallback: ((Int, Bundle?) -> Unit)
    ) = if (mediaBrowser.isConnected) {
        mediaController.sendCommand(command, parameters, object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                resultCallback(resultCode, resultData)
            }
        })
        true
    } else {
        false
    }


    private inner class MediaBrowserConnectionCallback(private val context: Context) :
        MediaBrowserCompat.ConnectionCallback() {

        // Invoked when MediaBrowser connection succeeds
        override fun onConnected() {
            // Get a MediaController for the MediaSession.
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken)
            mediaController.registerCallback(MediaControllerCallback())
            isConnected.postValue(true)
        }

        // Invoked when the client is disconnected from the media browser.
        override fun onConnectionSuspended() {
            isConnected.postValue(false)
        }

        // Invoked when the connection to the media browser failed.
        override fun onConnectionFailed() {
            isConnected.postValue(false)
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onRepeatModeChanged(repeatMode: Int) {
            this@MediaSessionConnection.repeatMode.postValue(repeatMode)
            preferences.edit().putInt(Constants.LAST_REPEAT_MODE, repeatMode).apply()
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            playbackState.postValue(state ?: EMPTY_PLAYBACK_STATE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            nowPlaying.postValue(metadata ?: NOTHING_PLAYING)
        }

        override fun onShuffleModeChanged(shuffleMode: Int) {
            this@MediaSessionConnection.shuffleMode.postValue(shuffleMode)
            preferences.edit().putInt(Constants.LAST_SHUFFLE_MODE, shuffleMode).apply()
        }

        /**
         * Normally if a [MediaBrowserServiceCompat] drops its connection the callback comes via
         * [MediaControllerCompat.Callback] (here). But since other connection status events
         * are sent to [MediaBrowserCompat.ConnectionCallback], we catch the disconnect here and
         * send it on to the other callback.
         */
        override fun onSessionDestroyed() {
            connectionCallback.onConnectionSuspended()
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            when (event) {
                Constants.NETWORK_FAILURE -> networkFailure.postValue(true)
            }
        }
    }

    companion object {
        // For Singleton instantiation.
        @Volatile
        private var instance: MediaSessionConnection? = null

        fun getInstance(context: Context, serviceComponent: ComponentName, preferences: SharedPreferences) =
            instance ?: synchronized(this) {
                instance ?: MediaSessionConnection(context, serviceComponent, preferences).also { instance = it }
            }
    }
}


val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()

val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()