// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.navigation

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import com.jadebyte.jadeplayer.R

/**
 * Created by Wilberforce on 09/04/2019 at 17:16.
 */
class NavRepository(private val preferences: SharedPreferences, private var origin: Int) {

    val liveItems = MediatorLiveData<List<NavItem>>()

    init {
        val items = arrayOfNulls<NavItem?>(9)
        items[preferences.getInt(SONGS, 0)] = NavItem(SONGS, R.string.songs, R.drawable.ic_song_thin, isFrom(0))
        items[preferences.getInt(IDENTIFY, 1)] =
            NavItem(IDENTIFY, R.string.identify_music, R.drawable.ic_waveforms, isFrom(1))
        items[preferences.getInt(ARTISTS, 2)] = NavItem(ARTISTS, R.string.artists, R.drawable.ic_microphone, isFrom(2))
        items[preferences.getInt(FAVOURITES, 3)] =
            NavItem(FAVOURITES, R.string.favourites, R.drawable.ic_heart, isFrom(3))
        items[preferences.getInt(GENRES, 4)] = NavItem(GENRES, R.string.genres, R.drawable.ic_guiter, isFrom(4))
        items[preferences.getInt(PLAYLIST, 5)] =
            NavItem(PLAYLIST, R.string.playlist, R.drawable.ic_playlist2, isFrom(5))
        items[preferences.getInt(RADIO, 6)] = NavItem(RADIO, R.string.radio, R.drawable.ic_radio, isFrom(6))
        items[preferences.getInt(SETTINGS, 7)] = NavItem(SETTINGS, R.string.settings, R.drawable.ic_settings, isFrom(7))
        items[preferences.getInt(VIDEOS, 8)] = NavItem(VIDEOS, R.string.videos, R.drawable.ic_video, isFrom(8))
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

const val SONGS = "com.jadebyte.jadeplayer.nav.songs"
const val IDENTIFY = "com.jadebyte.jadeplayer.nav.identify"
const val ARTISTS = "com.jadebyte.jadeplayer.nav.artists"
const val FAVOURITES = "com.jadebyte.jadeplayer.nav.favourites"
const val GENRES = "com.jadebyte.jadeplayer.nav.genres"
const val PLAYLIST = "com.jadebyte.jadeplayer.nav.playlist"
const val RADIO = "com.jadebyte.jadeplayer.nav.radio"
const val SETTINGS = "com.jadebyte.jadeplayer.nav.settings"
const val VIDEOS = "com.jadebyte.jadeplayer.nav.videos"

val keys = arrayListOf(SONGS, IDENTIFY, ARTISTS, FAVOURITES, GENRES, PLAYLIST, RADIO, SETTINGS, VIDEOS)