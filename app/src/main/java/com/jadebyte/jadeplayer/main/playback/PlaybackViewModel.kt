// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback

import android.app.Application
import android.database.ContentObserver
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jadebyte.jadeplayer.main.songs.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Wilberforce on 2019-05-18 at 21:55.
 */
class PlaybackViewModel(application: Application) : AndroidViewModel(application) {
    private val indexOfPlayingSong = MutableLiveData<Int>()
    private val songs = MutableLiveData<List<Song>>()
    val mediatorLiveData = MediatorLiveData<Any>()

    private var repository = PlaybackRepository(application)



    // TODO: Remove and change to Room
    private val observer: ContentObserver = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean) {
            loadData()
        }
    }


    fun loadData() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.loadData()
            }

            songs.value = result

        }
    }

    init {
        // TODO: Remove and change to Room
        observer.onChange(false)
        getApplication<Application>().contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )
        mediatorLiveData.addSource(songs) { mediatorLiveData.value = it }
        mediatorLiveData.addSource(indexOfPlayingSong) { mediatorLiveData.value = it }
    }

    fun updateIndexOfPlayingSong(index: Int) {
        indexOfPlayingSong.value = index
    }


    override fun onCleared() {
        super.onCleared()
        // TODO: Remove and change to Room
        getApplication<Application>().contentResolver.unregisterContentObserver(observer)
    }

}