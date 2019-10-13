// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist

import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hunter.library.debug.HunterDebug
import com.jadebyte.jadeplayer.main.common.event.Event
import com.jadebyte.jadeplayer.main.songs.Song
import com.jadebyte.jadeplayer.main.songs.SongsViewModel
import com.jadebyte.jadeplayer.main.songs.baseSongsProjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PlaylistSongsEditorViewModel(application: Application) : SongsViewModel(application) {
    private val playlistSongsRepository = PlaylistSongsRepository(application)
    private val playlistSongsProjection =
        listOf(*baseSongsProjection, MediaStore.Audio.Playlists.Members.AUDIO_ID).toTypedArray()
    private lateinit var playlistSongsUri: Uri
    private lateinit var initiallySelectedItems: List<Song>
    private val _playlistValue = MutableLiveData<Event<Boolean>>()
    val playlistValue: LiveData<Event<Boolean>> get() = _playlistValue


    override fun init(vararg params: Any?) {
        playlistSongsUri = MediaStore.Audio.Playlists.Members.getContentUri("external", params[0] as Long)
        super.init()
    }

    fun reverseSelection(index: Int): Boolean {
        return data.value?.let {
            if (it.size > index) {
                it[index].selected = !it[index].selected
                true
            } else false
        } ?: false

    }

    /**
     *  Preset all songs on [items] that exists in the playlist and post the modified items
     *  @param items all songs on the device
     */
    @HunterDebug
    override fun deliverResult(items: List<Song>) {
        // Find all the songs that belong to the playlist and select them
        MediaStore.Audio.Playlists.Members.AUDIO_ID
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val songsInPlaylist = playlistSongsRepository.loadData(playlistSongsUri, playlistSongsProjection)
                items.forEach { song ->
                    // The tracks are the same if they have the same titleKey and the same file paths
                    val playlistSong =
                        songsInPlaylist.firstOrNull { it.titleKey == song.titleKey && it.path == it.path }
                    if (playlistSong != null) {
                        song.selected = true
                        song.audioId = playlistSong.audioId
                    }
                }
                initiallySelectedItems = items.filter { it.selected }
                if (data.value != items) data.postValue(items)
            }
        }
    }

    fun updatePlaylist() {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                val selectedItems: List<Song> = items.value!!.filter { it.selected }
                // Items that weren't selected initially.
                // A better explanation: Items that doesn't exist in this playlist but are now selected
                val addableItems: List<Song> = selectedItems.minus(initiallySelectedItems)

                // Items that were initially selected but no longer selected
                val removableItems = initiallySelectedItems.minus(selectedItems)


                if (addableItems.isNotEmpty()) {
                    // Add songs to playlist
                    if (!addSongs(addableItems)) {
                        return@withContext false
                    }
                }

                if (!removableItems.isNullOrEmpty()) {
                    // Remove songs from playlist
                    if (!deleteSongs(removableItems)) {
                        return@withContext false
                    }

                }
                return@withContext true

            }
            _playlistValue.value = Event(success)
        }
    }

    @WorkerThread
    @HunterDebug
    private fun deleteSongs(songs: List<Song>): Boolean {
        val string = songs.map { it.audioId }.joinToString(", ")
        val where = MediaStore.Audio.Playlists.Members.AUDIO_ID + " IN ($string)"
        val deletedRows = getApplication<Application>().contentResolver?.delete(playlistSongsUri, where, null) ?: 0
        return deletedRows > 0

    }

    @WorkerThread
    @HunterDebug
    private fun addSongs(songs: List<Song>): Boolean {
        val contentValues = Array(songs.size) { ContentValues() }
        songs.forEachIndexed { index, song ->
            val value = contentValues[index]
            value.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, initiallySelectedItems.size + 1)
            value.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, song.id)
        }
        val addedRows = getApplication<Application>().contentResolver?.bulkInsert(playlistSongsUri, contentValues) ?: 0
        return addedRows > 0
    }
}