// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.view

import androidx.fragment.app.Fragment
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.utils.TimeUtils
import com.jadebyte.jadeplayer.main.common.utils.Utils
import com.jadebyte.jadeplayer.main.songs.Song
import java.util.concurrent.TimeUnit

/**
 * Created by Wilberforce on 2019-04-21 at 11:56.
 */
open class BaseFragment : Fragment() {
    fun isPermissionGranted(permission: String): Boolean = Utils.isPermissionGranted(permission, activity)

    fun getSongsTotalTime(songs: List<Song>): CharSequence? {
        val secs = TimeUnit.MILLISECONDS.toSeconds(TimeUtils.getTotalSongsDuration(songs))
        return getString(
            R.string.two_comma_separated_values,
            resources.getQuantityString(R.plurals.numberOfSongs, songs.size, songs.size),
            TimeUtils.formatElapsedTime(secs, activity)
        )
    }
}