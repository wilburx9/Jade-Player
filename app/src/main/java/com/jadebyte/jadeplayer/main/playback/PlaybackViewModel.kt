// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback

import android.app.Application
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.SubscriptionCallback
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import com.jadebyte.jadeplayer.main.common.data.Constants
import com.jadebyte.jadeplayer.main.explore.RecentlyPlayedRepository
import com.jadebyte.jadeplayer.main.explore.RecentlyPlayedRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Wilberforce on 2019-05-18 at 21:55.
 */
class PlaybackViewModel(
    application: Application,
    mediaSessionConnection: MediaSessionConnection,
    private val preferences: SharedPreferences,
    private val mediaId: String
) :
    AndroidViewModel(application) {

    private val playedRepository: RecentlyPlayedRepository
    private val _mediaItems = MutableLiveData<List<MediaItemData>>()
    private val _currentItem = MutableLiveData<MediaItemData?>()
    private val _playbackState = MutableLiveData<PlaybackStateCompat>().apply { value = EMPTY_PLAYBACK_STATE }
    private val _mediaPosition =
        MutableLiveData<Long>().apply { value = preferences.getLong(Constants.LAST_POSITION, 0) }
    private var updatePosition = true
    private val handler = Handler(Looper.getMainLooper())

    val mediaItems: LiveData<List<MediaItemData>> = _mediaItems
    val currentItem: LiveData<MediaItemData?> = _currentItem
    val playbackState: LiveData<PlaybackStateCompat> = _playbackState
    val mediaPosition: LiveData<Long> = _mediaPosition


    init {
        val recentlyPlayed = RecentlyPlayedRoomDatabase.getDatabase(application).recentDao()
        playedRepository = RecentlyPlayedRepository(recentlyPlayed)
    }

    fun playCurrent() = playMediaId(currentItem.value?.id)

    fun playMediaId(mediaId: String?) {
        if (mediaId == null) return
        val nowPlaying = mediaSessionConnection.nowPlaying.value
        val transportControls = mediaSessionConnection.transportControls

        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaId == nowPlaying?.id) {
            mediaSessionConnection.playbackState.value?.let {
                when {
                    it.isPlayingOrBuffering -> transportControls.pause()
                    it.isPauseEnabled -> transportControls.play()
                }
            }
        } else {
            transportControls.playFromMediaId(mediaId, null)
        }
    }

    fun skipToNext() {
        if (mediaSessionConnection.playbackState.value?.started == true) {
            mediaSessionConnection.transportControls.skipToNext()
        } else {
            _mediaItems.value?.let {
                val i = it.indexOf(currentItem.value)
                // Only skip to the next item if the current item is not the last item in the list
                if (i != (it.size - 1)) _currentItem.postValue(it[(i + 1)])
            }
        }
    }

    fun skipToPrevious() {
        if (mediaSessionConnection.playbackState.value?.started == true) {
            mediaSessionConnection.transportControls.skipToPrevious()
        } else {
            _mediaItems.value?.let {
                val i = it.indexOf(currentItem.value)
                // Only skip to the previous item if the current item is not first item in the list
                if (i > 1) _currentItem.postValue(it[(i - 1)])
            }
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


    private fun updateState(state: PlaybackStateCompat, metadata: MediaMetadataCompat):
            List<MediaItemData>? {
        val items = (_mediaItems.value?.map { it.copy(isPlaying = it.id == metadata.id && state.isPlayingOrBuffering) }
            ?: emptyList())

        val currentItem = if (items.isEmpty()) {
            // Only update media item if playback has started
            if (state.started) {
                MediaItemData(metadata, state.isPlaying, state.isBuffering)
            } else {
                null
            }
        } else {
            // Only update media item once we have duration available
            if (metadata.duration != 0L && items.isNotEmpty()) {
                val matchingItem = items.first { it.id == metadata.id }
                matchingItem.apply {
                    isPlaying = state.isPlaying
                    isBuffering = state.isBuffering
                    duration = metadata.duration
                }
            } else null
        }

        // Update synchronously so addToRecentlyPlayed can pick up a valid currentItem
        if (currentItem != null) _currentItem.value = currentItem
        _playbackState.postValue(state)
        if (state.started) updatePlaybackPosition()
        return items
    }

    private val subscriptionCallback = object : SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
            val items = children.map { MediaItemData(it, isItemPlaying(it.mediaId!!), isItemBuffering(it.mediaId!!)) }
            val current = (items.firstOrNull { it.isPlaying }
                ?: items.firstOrNull { it.id == preferences.getString(Constants.LAST_ID, null) }
                ?: items.firstOrNull())

            viewModelScope.launch {
                // Let's get the duration of the current playing song if it's the same as our filter above
                val currentValue = currentItem.value
                if (current != null) {
                    if (currentValue != null && (current.id == currentValue.id)) {
                        current.duration = currentValue.duration
                    } else {
                        val value = withContext(Dispatchers.IO) {
                            playedRepository.fetchFirst()
                        }
                        current.duration = value?.duration ?: 0
                    }
                }
                _mediaItems.postValue(items)
                _currentItem.postValue(current)
                // Re-post the media position so views like SeekBars can pickup the new view
                _mediaPosition.postValue(mediaPosition.value)
            }
        }
    }

    private fun isItemPlaying(mediaId: String): Boolean {
        val isActive = mediaId == mediaSessionConnection.nowPlaying.value?.id
        val isPlaying = mediaSessionConnection.playbackState.value?.isPlaying ?: false
        return isActive && isPlaying
    }

    private fun isItemBuffering(mediaId: String): Boolean {
        val isActive = mediaId == mediaSessionConnection.nowPlaying.value?.id
        val isBuffering = mediaSessionConnection.playbackState.value?.isBuffering ?: false
        return isActive && isBuffering
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
        it.subscribe(mediaId, subscriptionCallback)
        it.playbackState.observeForever(playbackStateObserver)
        it.nowPlaying.observeForever(mediaMetadataObserver)
    }


    /**
     * Internal function that recursively calls itself every [POSITION_UPDATE_INTERVAL_MILLIS] ms
     * to check the current playback position and updates the corresponding LiveData object when it
     * has changed.
     */
    private fun updatePlaybackPosition(): Boolean = handler.postDelayed({
        val currPosition = _playbackState.value?.currentPlayBackPosition
        if (_mediaPosition.value != currPosition) {
            _mediaPosition.postValue(currPosition)
        }
        if (updatePosition)
            updatePlaybackPosition()
    }, POSITION_UPDATE_INTERVAL_MILLIS)

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

        // Stop updating the position
        updatePosition = false

        handler.removeCallbacksAndMessages(null)
    }

}

private const val POSITION_UPDATE_INTERVAL_MILLIS = 100L