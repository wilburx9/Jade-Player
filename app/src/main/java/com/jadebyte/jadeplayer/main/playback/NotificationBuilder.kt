// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.
// Very little modification was made from the original file: https://github.com/googlesamples/android-UniversalMusicPlayer/raw/master/common/src/main/java/com/example/android/uamp/media/NotificationBuilder.kt

package com.jadebyte.jadeplayer.main.playback

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media.session.MediaButtonReceiver
import com.jadebyte.jadeplayer.R


/**
 * Created by Wilberforce on 2019-08-22 at 21:30.
 */
class NotificationBuilder(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    private val skipToPrevious = NotificationCompat.Action(
        R.drawable.ic_skip_previous_notif,
        context.getString(R.string.skip_to_previous),
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
    )
    private val playAction = NotificationCompat.Action(
        R.drawable.ic_play_notif,
        context.getString(R.string.play_song),
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY)
    )

    private val pauseAction = NotificationCompat.Action(
        R.drawable.ic_pause_notif,
        context.getString(R.string.pause_song),
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PAUSE)
    )
    private val skipToNextAction = NotificationCompat.Action(
        R.drawable.ic_skip_next_notif,
        context.getString(R.string.skip_to_next),
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
    )

    private val stopPendingIntent =
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP)

    fun buildNotification(sessionToken: MediaSessionCompat.Token, largeIcon: Bitmap? = null): Notification {
        if (shouldCreatePlaybackChannel()) createPlaybackChannel()

        val controller = MediaControllerCompat(context, sessionToken)
        val description = controller.metadata.description
        val state = controller.playbackState

        val builder = NotificationCompat.Builder(context, PLAYBACK_CHANNEL)

        // Only add actions for skip back, play/pause, skip forward, based on what's enabled.
        val actions = mutableListOf<Int>()
        if (state.isSkipToPreviousEnabled) {
            builder.addAction(skipToPrevious)
            actions.add(0)
        }

        if (state.isPlayingOrBuffering) {
            builder.addAction(pauseAction)
            actions.add(1)
        } else if (state.isPlayEnabled) {
            builder.addAction(playAction)
            actions.add(1)
        }

        if (state.isSkipToNextEnabled) {
            builder.addAction(skipToNextAction)
            actions.add(2)
        }

        val mediaStyle = MediaStyle()
            .setCancelButtonIntent(stopPendingIntent)
            .setMediaSession(sessionToken)
            .setShowActionsInCompactView(*actions.toIntArray())
            .setShowCancelButton(true)

        return builder.setContentIntent(controller.sessionActivity)
            .setContentText(description.subtitle)
            .setContentTitle(description.title)
            .setDeleteIntent(stopPendingIntent)
            .setOnlyAlertOnce(true)
            .setLargeIcon(largeIcon ?: description.iconBitmap)
            .setSmallIcon(R.drawable.ic_notification)
            .setStyle(mediaStyle)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }


    private fun shouldCreatePlaybackChannel() =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !playbackChannelExists()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun playbackChannelExists() = notificationManager.getNotificationChannel(PLAYBACK_CHANNEL) != null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createPlaybackChannel() {
        val notificationChannel = NotificationChannel(
            PLAYBACK_CHANNEL,
            context.getString(R.string.notification_channel),
            NotificationManager.IMPORTANCE_LOW
        )
        notificationChannel.description = context.getString(R.string.notification_channel_description)
        notificationManager.createNotificationChannel(notificationChannel)
    }


}


const val PLAYBACK_CHANNEL: String = "com.jadebyte.jadeplayer.main.playback.NOW_PLAYING"
