// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback

import android.app.Application
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.SubscriptionCallback
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import timber.log.Timber

/**
 * Created by Wilberforce on 2019-05-18 at 21:55.
 */
class PlaybackViewModel(application: Application, mediaSessionConnection: MediaSessionConnection) :
    AndroidViewModel(application) {

    private val _mediaItems = MutableLiveData<List<MediaItemData>>()
    val mediaItems: LiveData<List<MediaItemData>> = _mediaItems

    fun playMediaId(mediaId: String) {
        val nowPlaying = mediaSessionConnection.nowPlaying.value
        val transportControls = mediaSessionConnection.transportControls

        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaId == nowPlaying?.id) {
            mediaSessionConnection.playbackState.value?.let {
                when {
                    it.isPlaying -> transportControls.pause()
                    it.isPauseEnabled -> transportControls.play()
                    else -> {
                        Timber.w(
                            "playMediaId: Playable item clicked but neither play nor pause are enabled!(mediaId=$mediaId)"
                        )
                    }
                }
            }
        } else {
            transportControls.playFromMediaId(mediaId, null)
        }
    }

    // When the session's [PlaybackStateCompat] changes, the [mediaItems] needs to be updated
    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        val state = it ?: EMPTY_PLAYBACK_STATE
        val metadata = mediaSessionConnection.nowPlaying.value ?: NOTHING_PLAYING
        if (metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) != null) {
            _mediaItems.postValue(updateState(state, metadata))
        }
    }

    // When the session's [MediaMetadataCompat] changes, the [mediaItems] needs to be updated
    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        val playbackState = mediaSessionConnection.playbackState.value ?: EMPTY_PLAYBACK_STATE
        val metadata = it ?: NOTHING_PLAYING
        if (metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) != null) {
            _mediaItems.postValue(updateState(playbackState, metadata))
        }
    }


    private fun updateState(state: PlaybackStateCompat, metadata: MediaMetadataCompat): List<MediaItemData>? {

        return _mediaItems.value?.map { it.copy(isPlaying = it.id == metadata.id && state.isPlaying) }
            ?: emptyList()
    }

    private val subscriptionCallback = object : SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
            val items = children.map { MediaItemData(it, isItemPlaying(it.mediaId!!)) }
            _mediaItems.postValue(items)
        }
    }

    private fun isItemPlaying(mediaId: String): Boolean {
        val isActive = mediaId == mediaSessionConnection.nowPlaying.value?.id
        val isPlaying = mediaSessionConnection.playbackState.value?.isPlaying ?: false
        return isActive && isPlaying
    }

    /**
     *  Because there's a complex dance between this [AndroidViewModel] and the [MediaSessionConnection]
     *  (which is wrapping a [MediaBrowserCompat] object), the usual guidance of using [Transformations]
     *  doesn't quite work.
     *
     *  Specifically there's three things that are watched that will cause the single piece of [LiveData]
     *  exposed from this class to be updated
     *
     *  [subscriptionCallback] (defined above) is called if/when the children of this ViewModel's [mediaId] changes
     *
     *  [MediaSessionConnection.playbackState] changes state based on the playback state of
     *  the player, which can change the [MediaItemData.isPlaying]s in the list.
     *
     *  [MediaSessionConnection.nowPlaying] changes based on the item that's being played,
     *  which can also change [MediaItemData.isPlaying]s in the list.
     */
    private val mediaSessionConnection = mediaSessionConnection.also {
        it.subscribe(mediaSessionConnection.rootMediaId, subscriptionCallback)
        it.playbackState.observeForever(playbackStateObserver)
        it.nowPlaying.observeForever(mediaMetadataObserver)
    }

    /**
     * Since we use [LiveData.observeForever] above (in [mediaSessionConnection]), we want
     * to call [LiveData.removeObserver] here to prevent leaking resources when the [ViewModel]
     * is not longer in use.
     *
     * For more details, see the kdoc on [mediaSessionConnection] above.
     */
    override fun onCleared() {
        super.onCleared()

        // Remove the permanent observers from the MediaSessionConnection.
        mediaSessionConnection.playbackState.removeObserver(playbackStateObserver)
        mediaSessionConnection.nowPlaying.removeObserver(mediaMetadataObserver)

        // And then, finally, unsubscribe the media ID that was being watched.
        mediaSessionConnection.unsubscribe(mediaSessionConnection.rootMediaId, subscriptionCallback)
    }

}