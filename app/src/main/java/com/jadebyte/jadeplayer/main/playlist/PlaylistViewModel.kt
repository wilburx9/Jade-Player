// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.viewModelScope
import com.hunter.library.debug.HunterDebug
import com.jadebyte.jadeplayer.main.common.data.BaseRepository
import com.jadebyte.jadeplayer.main.common.data.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Wilberforce on 2019-05-19 at 10:11.
 */
class PlaylistViewModel(application: Application) : BaseViewModel<Playlist>(application) {

    override var repository: BaseRepository<Playlist> = PlaylistRepository(application)

    override var sortOrder: String? = "${MediaStore.Audio.Playlists.DATE_MODIFIED} COLLATE NOCASE DESC"

    override var uri: Uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI

    override var projection: Array<String>? = arrayOf(
        MediaStore.Audio.Playlists._ID,
        MediaStore.Audio.Playlists.NAME,
        MediaStore.Audio.Playlists.DATE_MODIFIED
    )


    fun init(playlistId: Long) {
        selection = "${MediaStore.Audio.Playlists._ID} == ?"
        selectionArgs = arrayOf(playlistId.toString())
        init()
    }

    @HunterDebug
    override fun deliverResult(items: List<Playlist>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                items.forEach {
                    it.songsCount = (repository as PlaylistRepository).fetchSongCount(it.id)
                }
                data.postValue(items)
            }
        }
    }

}