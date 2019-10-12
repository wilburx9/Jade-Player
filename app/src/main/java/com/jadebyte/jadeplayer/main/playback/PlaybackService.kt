// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.
// Modifications was made from the original file: https://github.com/googlesamples/android-UniversalMusicPlayer/raw/master/common/src/main/java/com/example/android/uamp/media/MusicService.kt

package com.jadebyte.jadeplayer.main.playback

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.FileDataSourceFactory
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.GlideApp
import com.jadebyte.jadeplayer.main.common.data.Constants
import com.jadebyte.jadeplayer.main.explore.RecentlyPlayed
import com.jadebyte.jadeplayer.main.explore.RecentlyPlayedRepository
import com.jadebyte.jadeplayer.main.explore.RecentlyPlayedRoomDatabase
import com.jadebyte.jadeplayer.main.songs.basicSongsOrder
import com.jadebyte.jadeplayer.main.songs.basicSongsSelection
import com.jadebyte.jadeplayer.main.songs.basicSongsSelectionArg
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

/**
 * This class is the entry point for browsing and playback commands from the APP's UI
 * and other apps that wish to play music via Jade Player (for example, Android Auto or
 * the Google Assistant).
 *
 * Browsing begins with the method [PlaybackService.onGetRoot], and continues in
 * the callback [onLoadChildren].
 */
class PlaybackService : MediaBrowserServiceCompat() {
    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver
    private lateinit var packageValidator: PackageValidator
    private lateinit var mediaSource: MusicSource
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaController: MediaControllerCompat
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notificationBuilder: NotificationBuilder
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var recentRepo: RecentlyPlayedRepository

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private val preferences: SharedPreferences by inject()
    private var isForegroundService = false


    override fun onCreate() {
        super.onCreate()

        // Build a PendingIntent that can be used to launch the UI.
        val sessionActivityPendingActivity = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, 0)
        }

        recentRepo = RecentlyPlayedRepository(RecentlyPlayedRoomDatabase.getDatabase(application).recentDao())

        // Create a MediaSession
        mediaSession = MediaSessionCompat(this, this.javaClass.name).apply {
            setSessionActivity(sessionActivityPendingActivity)
            isActive = true
        }

        // In order for [MediaBrowserCompat.ConnectionCallback.onConnected] to be called,
        // a [MediaSessionCompat.Token] needs to be set on the [MediaBrowserServiceCompat].
        // Note that this must be set by the time [onGetRoot] returns otherwise the connection will fail silently
        // and the system will not even call [MediaBrowserCompat.ConnectionCallback.onConnectionFailed]
        sessionToken = mediaSession.sessionToken

        // Because ExoPlayer will manage the MediaSession, add the service as a callback for
        // state changes.
        mediaController = MediaControllerCompat(this, mediaSession).also {
            it.registerCallback(MediaControllerCallback())
        }

        notificationBuilder = NotificationBuilder(this)
        notificationManager = NotificationManagerCompat.from(this)
        becomingNoisyReceiver = BecomingNoisyReceiver(this, mediaSession.sessionToken)


        // The media library is built from the MediaStore. We'll create the source here, and then use
        // a suspend function to perform the query and initialization off the main thread
        mediaSource = MediaStoreSource(
            this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, basicSongsSelection, arrayOf(basicSongsSelectionArg),
            basicSongsOrder
        )
        serviceScope.launch {
            mediaSource.load()
        }

        // ExoPlayer will manage the MediaSession for us.
        mediaSessionConnector = MediaSessionConnector(mediaSession).also {
            // Produces DataSource instances through which media data is loaded.
            val dataSourceFactory = FileDataSourceFactory()
            // Create the PlaybackPreparer of the media session connector.
            val playbackPreparer = PlaybackPreparer(
                mediaSource,
                exoPlayer,
                dataSourceFactory,
                preferences
            )
            it.setPlayer(exoPlayer)
            it.setPlaybackPreparer(playbackPreparer)
            it.setQueueNavigator(QueueNavigator(mediaSession))
        }
        packageValidator = PackageValidator(this, R.xml.allowed_media_browser_callers)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.isActive = false
        mediaSession.release()

        // Cancel coroutines when the service is going away.
        serviceScope.cancel()
    }

    // Removes the playback notification.
    // Since `stopForeground(false)` has already been called in [MediaControllerCallback.onPlaybackStateChanged],
    // it's possible to cancel the notification to cancel the notification with
    // `notificationManager.cancel(PLAYBACK_NOTIFICATION)` if minSdkVersion is >= [Build.VERSION_CODES.LOLLIPOP].
    //
    // Prior to [Build.VERSION_CODES.LOLLIPOP], notifications associated with a foreground service remained marked
    // "ongoing" even after calling [Service.stopForeground], and cannot be cancelled normally.
    //
    // Fortunately, it's possible to simple call [Service.stopForeground] a second time, this time with `true`.
    // his won't change anything about the service's state, but will simply remove the notification.
    private fun removePlaybackNotification() = stopForeground(true)

    // Returns a list of [MediaItem]s that match the given search query
    override fun onSearch(query: String, extras: Bundle?, result: Result<List<MediaItem>>) {
        val resultSent = mediaSource.whenReady { initialized ->
            if (initialized) {
                val resultList =
                    mediaSource.search(query, extras ?: Bundle.EMPTY).map { MediaItem(it.description, it.flag) }
                result.sendResult(resultList)
            }
        }
        if (!resultSent) {
            result.detach()
        }
    }

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


    // This must be `by lazy` because the source won't initially be ready.
    // See [onLoadChildren] to see where it's accessed (and first constructed)
    private val browseTree by lazy {
        BrowseTree(applicationContext, mediaSource)
    }

    private val audioAttributes = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    // Configure ExoPlayer to handle audio focus for us.
    // See https://link.medium.com/Zw5gorq9mZ
    private val exoPlayer by lazy {
        ExoPlayerFactory.newSimpleInstance(this).apply {
            setAudioAttributes(this@PlaybackService.audioAttributes, true)
        }
    }


    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        val notificationTarget = NotificationTarget()
        var largeBitmap: Bitmap? = null


        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            updateNotification(mediaController.playbackState)
            addToRecentlyPlayed(metadata, mediaController.playbackState)
            persistPosition()
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            updateNotification(state)
            addToRecentlyPlayed(mediaController.metadata, state)
            persistPosition()
        }

        private fun updateNotification(state: PlaybackStateCompat?) {
            if (state == null) return

            when (val updatedState = state.state) {
                PlaybackStateCompat.STATE_PLAYING,
                PlaybackStateCompat.STATE_BUFFERING -> initiatePlayback(updatedState)
                else -> terminatePlayback(updatedState)
            }
        }

        private fun terminatePlayback(state: Int) {
            becomingNoisyReceiver.unregister()
            if (isForegroundService) {
                stopForeground(false)
                isForegroundService = false

                // If playback has ended, also stop the service
                if (state == PlaybackStateCompat.STATE_NONE) {
                    stopSelf()
                }
                val notification = buildNotification(state)
                if (notification != null) {
                    notificationManager.notify(Constants.PLAYBACK_NOTIFICATION, notification)
                } else {
                    removePlaybackNotification()
                }
            }
        }

        private fun initiatePlayback(state: Int) {
            becomingNoisyReceiver.register()

            // This may look strange, but the documentation for [Service.startForeground]
            // notes that "calling this method does *not* put the service in the started
            // state itself, even though the name sounds like it."
            buildNotification(state)?.let {
                notificationManager.notify(Constants.PLAYBACK_NOTIFICATION, it)
                loadLargeIcon()

                if (!isForegroundService) {
                    ContextCompat.startForegroundService(
                        applicationContext, Intent(applicationContext, this@PlaybackService.javaClass)
                    )
                    startForeground(Constants.PLAYBACK_NOTIFICATION, it)
                    isForegroundService = true
                }
            }
        }

        private fun loadLargeIcon() {
            GlideApp.with(this@PlaybackService)
                .asBitmap()
                .skipMemoryCache(false)
                .load(mediaController.metadata.description.iconUri)
                .into(notificationTarget)
        }

        private fun buildNotification(state: Int): Notification? {

            // Skip building a notification when state is "none" and metadata is null
            return if (mediaController.metadata != null
                && state != PlaybackStateCompat.STATE_NONE
            ) {
                notificationBuilder.buildNotification(mediaSession.sessionToken, largeBitmap)
            } else {
                null
            }
        }

        private fun addToRecentlyPlayed(metadata: MediaMetadataCompat?, state: PlaybackStateCompat?) {
            if (metadata?.id != null && state?.isPlaying == true) {
                serviceScope.launch {
                    val played = RecentlyPlayed(metadata)
                    recentRepo.insert(played)
                    recentRepo.trim()
                    preferences.edit().putString(Constants.LAST_ID, metadata.id).apply()
                }

            }
        }

        private fun persistPosition() {
            if (mediaController.playbackState.started) {
                preferences.edit().putLong(Constants.LAST_POSITION, exoPlayer.contentPosition).apply()
            }
        }


        private inner class NotificationTarget :
            CustomTarget<Bitmap>(NOTIFICATION_LARGE_ICON_SIZE, NOTIFICATION_LARGE_ICON_SIZE) {

            override fun onStart() {
                largeBitmap = null
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                largeBitmap = null
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                largeBitmap = null
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                notificationManager.notify(
                    Constants.PLAYBACK_NOTIFICATION, notificationBuilder.buildNotification
                        (mediaSession.sessionToken, resource)
                )
                largeBitmap = resource
            }

        }


    }
}

// Helper class to retrieve the the Metadata necessary for the ExoPlayer MediaSession connection
// extension to call [MediaSessionCompat.setMetadata].
private class QueueNavigator(mediaSession: MediaSessionCompat) : TimelineQueueNavigator(mediaSession) {

    private val window = Timeline.Window()

    override fun getMediaDescription(player: Player, windowIndex: Int) =
        player.currentTimeline.getWindow(windowIndex, window, true).tag as MediaDescriptionCompat

}

