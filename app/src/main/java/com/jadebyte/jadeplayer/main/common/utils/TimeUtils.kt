// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.utils

import android.content.Context
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.songs.Song
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit


/**
 * Created by Wilberforce on 2019-04-22 at 01:30.
 */
object TimeUtils {


    /**
     * Converts time to HH:MM:SS and removes leading zeros
     */
    fun durationToHMS(millis: Long): String {
        return String.format(
            "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
        ).replace(Regex("^00:"), "")

    }

    /**
     * Returns the total duration of a given list of songs
     */
    fun getTotalSongsDuration(list: List<Song>): Long {
        return if (list.isEmpty()) 0 else list.map { it.duration }.reduce { sum, element -> sum + element }
    }


    /**
     * Formats an elapsed time in a format like "MM:SS" or "H:MM:SS" (using a form
     * suited to the current locale), similar to that used on the call-in-progress
     * screen. Copied from [android.text.format.DateUtils.formatElapsedTime]
     *
     * @param secs the elapsed time in seconds.
     */
    fun formatElapsedTime(secs: Long, cxt: Context?): String {
        var elapsedSeconds = secs
        val context = WeakReference(cxt).get()
        context?.let {
            // Break the elapsed seconds into hours, minutes, and seconds.
            var hours: Long = 0
            var minutes: Long = 0


            if (elapsedSeconds >= 3600) {
                hours = elapsedSeconds / 3600
                elapsedSeconds -= hours * 3600
            }
            if (elapsedSeconds >= 60) {
                minutes = elapsedSeconds / 60
                elapsedSeconds -= minutes * 60
            }
            val seconds = elapsedSeconds

            val hrStr = context.getString(if (hours != 1L) R.string.hours else R.string.hour)
            val minStr = context.getString(if (minutes != 1L) R.string.mins else R.string.min)
            val secStr = context.getString(if (seconds != 1L) R.string.secs else R.string.sec)
            val comma = context.getString(R.string.comma)

            val sb = StringBuilder()
            when {
                hours > 0 -> sb.append(hours).append(hrStr).append(comma).append(minutes).append(minStr)
                    .append(comma).append(seconds).append(secStr).toString()
                minutes > 0 -> sb.append(minutes).append(minStr).append(comma).append(seconds).append(secStr).toString()
                else -> sb.append(seconds).append(secStr).toString()
            }
            return sb.toString()
        }
        return ""
    }
}
