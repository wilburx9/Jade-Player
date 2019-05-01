// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback

import android.app.Application
import android.database.ContentObserver
import android.provider.MediaStore
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jadebyte.jadeplayer.main.songs.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Wilberforce on 2019-05-01 at 18:46.
 * ViewModel that gets the last played song
 */
class BottomPlaybackViewModel(application: Application) : AndroidViewModel(application) {
    var repository = BottomPlaybackRepository(application)
    val song = ObservableField<Song>()

    fun init() {
        // TODO: Remove and change to Room
        observer.onChange(false)
        getApplication<Application>().contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )
    }


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

            song.set(result)

        }
    }

    override fun onCleared() {
        super.onCleared()
        // TODO: Remove and change to Room
        getApplication<Application>().contentResolver.unregisterContentObserver(observer)
    }
}