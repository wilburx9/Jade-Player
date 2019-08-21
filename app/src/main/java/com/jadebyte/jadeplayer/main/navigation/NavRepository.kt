// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.navigation

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.data.Constants

/**
 * Created by Wilberforce on 09/04/2019 at 17:16.
 */
class NavRepository(private var origin: Int?, private val preferences: SharedPreferences) {

    val liveItems = MediatorLiveData<List<NavItem>>()

    init {
        val items = arrayOfNulls<NavItem?>(9)
        items[preferences.getInt(Constants.NAV_SONGS, 0)] =
            NavItem(Constants.NAV_SONGS, R.string.songs, R.drawable.ic_song_thin, isFrom(0))
        items[preferences.getInt(Constants.NAV_IDENTIFY, 1)] =
            NavItem(Constants.NAV_IDENTIFY, R.string.identify_music, R.drawable.ic_waveforms, isFrom(1))
        items[preferences.getInt(Constants.NAV_ARTISTS, 2)] =
            NavItem(Constants.NAV_ARTISTS, R.string.artists, R.drawable.ic_microphone, isFrom(2))
        items[preferences.getInt(Constants.NAV_FAVOURITES, 3)] =
            NavItem(Constants.NAV_FAVOURITES, R.string.favourites, R.drawable.ic_heart, isFrom(3))
        items[preferences.getInt(Constants.NAV_GENRES, 4)] =
            NavItem(Constants.NAV_GENRES, R.string.genres, R.drawable.ic_guiter, isFrom(4))
        items[preferences.getInt(Constants.NAV_PLAYLIST, 5)] =
            NavItem(Constants.NAV_PLAYLIST, R.string.playlist, R.drawable.ic_playlist2, isFrom(5))
        items[preferences.getInt(Constants.NAV_RADIO, 6)] =
            NavItem(Constants.NAV_RADIO, R.string.radio, R.drawable.ic_radio, isFrom(6))
        items[preferences.getInt(Constants.NAV_SETTINGS, 7)] =
            NavItem(Constants.NAV_SETTINGS, R.string.settings, R.drawable.ic_settings, isFrom(7))
        items[preferences.getInt(Constants.NAV_VIDEOS, 8)] =
            NavItem(Constants.NAV_VIDEOS, R.string.videos, R.drawable.ic_video, isFrom(8))
        liveItems.value = items.filterNotNull()
    }


    fun swap(list: List<NavItem>) {
        val editor = preferences.edit()
        list.forEachIndexed { i, item ->
            editor.putInt(item.id, i)
        }
        editor.apply()
    }


    /**
     * Check if [NavigationDialogFragment] is launched from this [NavItem]
     * @param originalIndex the index of the item before the user modified it.
     * Corresponds to the default values of the index of the item passed to SharedPreferences
     */
    private fun isFrom(originalIndex: Int): Boolean = originalIndex == origin
}

val keys = arrayListOf(
    Constants.NAV_SONGS,
    Constants.NAV_IDENTIFY,
    Constants.NAV_ARTISTS,
    Constants.NAV_FAVOURITES,
    Constants.NAV_GENRES,
    Constants.NAV_PLAYLIST,
    Constants.NAV_RADIO,
    Constants.NAV_SETTINGS,
    Constants.NAV_VIDEOS
)