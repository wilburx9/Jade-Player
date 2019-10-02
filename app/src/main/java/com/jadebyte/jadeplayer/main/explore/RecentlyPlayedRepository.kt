// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.explore

import androidx.lifecycle.LiveData


/**
 * Created by Wilberforce on 2019-09-15 at 07:39.
 */

class RecentlyPlayedRepository(private val playedDao: RecentlyPlayedDao) {

    val recentlyPlayed: LiveData<List<RecentlyPlayed>> = playedDao.fetchAll()

    suspend fun insert(recentlyPlayed: RecentlyPlayed) = playedDao.insert(recentlyPlayed)

    suspend fun trim() = playedDao.trim()

    suspend fun fetchFirst(): RecentlyPlayed? = playedDao.fetchFirst()
}