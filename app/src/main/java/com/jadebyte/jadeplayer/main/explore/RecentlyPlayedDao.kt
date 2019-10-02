// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.explore

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jadebyte.jadeplayer.main.common.data.Constants


/**
 * Created by Wilberforce on 2019-09-15 at 06:53.
 */
@Dao
interface RecentlyPlayedDao {
    @Query("SELECT * FROM recently_played_table ORDER BY entryDate DESC")
    fun fetchAll(): LiveData<List<RecentlyPlayed>>

    @Query("SELECT * FROM recently_played_table ORDER BY entryDate DESC LIMIT 1")
    suspend fun fetchFirst(): RecentlyPlayed?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentlyPlayed: RecentlyPlayed)

    /**
     * We want to keep a maximum of [Constants.MAX_RECENTLY_PLAYED] items in this database
     *
     * This will delete the rows whose id is greater than [Constants.MAX_RECENTLY_PLAYED]
     */
    @Query("DELETE FROM recently_played_table where id NOT IN (SELECT id from recently_played_table ORDER BY entryDate DESC LIMIT ${Constants.MAX_RECENTLY_PLAYED})")
    suspend fun trim()

}