// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.explore

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


/**
 * Created by Wilberforce on 2019-09-15 at 07:13.
 */
@Database(entities = [RecentlyPlayed::class], version = 1)
abstract class RecentlyPlayedRoomDatabase : RoomDatabase() {

    abstract fun recentDao(): RecentlyPlayedDao

    companion object {
        @Volatile
        private var INSTANCE: RecentlyPlayedRoomDatabase? = null

        fun getDatabase(context: Context): RecentlyPlayedRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder<RecentlyPlayedRoomDatabase>(
                    context.applicationContext, RecentlyPlayedRoomDatabase::class.java,
                    "recently_played_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}