// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.explore

import androidx.lifecycle.LiveData


/**
 * Created by Wilberforce on 2019-09-15 at 07:39.
 */

class RecentlyPlayedRepository(private val recentlyPlayedDao: RecentlyPlayedDao) {
    val recentlyPlayed: LiveData<List<RecentlyPlayed>> = recentlyPlayedDao.fetchRecentSongs()

    suspend fun insert(recentlyPlayed: RecentlyPlayed) = recentlyPlayedDao.insert(recentlyPlayed)

    suspend fun trim() = recentlyPlayedDao.trim()
}