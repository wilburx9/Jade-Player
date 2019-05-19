// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import com.jadebyte.jadeplayer.main.songs.SongsViewModel

/**
 * Created by Wilberforce on 2019-05-18 at 21:55.
 */
class PlaybackViewModel(application: Application) : SongsViewModel(application) {
    private val indexOfPlayingSong = MediatorLiveData<Int>()
    val mediatorLiveData = MediatorLiveData<Any>()



    init {
        init()
        mediatorLiveData.addSource(data) { mediatorLiveData.value = it }
        mediatorLiveData.addSource(indexOfPlayingSong) { mediatorLiveData.value = it }
    }

    fun updateIndexOfPlayingSong(index: Int) {
        indexOfPlayingSong.value = index
    }

}