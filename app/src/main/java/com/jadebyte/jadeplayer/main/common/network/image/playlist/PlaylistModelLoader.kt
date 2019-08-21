// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image.playlist

import android.net.Uri
import android.provider.MediaStore
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import com.jadebyte.jadeplayer.common.App
import com.jadebyte.jadeplayer.main.common.network.image.BaseCollageDataFetcher
import com.jadebyte.jadeplayer.main.common.utils.ImageUtils
import com.jadebyte.jadeplayer.main.playlist.Playlist
import java.io.File
import java.io.InputStream

/**
 * Created by Wilberforce on 2019-06-07 at 04:35.
 */
class PlaylistModelLoader :
    ModelLoader<Playlist, InputStream> {

    override fun buildLoadData(
        model: Playlist,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(ObjectKey(model), PlaylistDataFetcher(model, width, height))
    }

    override fun handles(model: Playlist) = true


    inner class PlaylistDataFetcher(private val playlist: Playlist, width: Int, height: Int) :
        BaseCollageDataFetcher(playlist, width, height, true) {

        override var modelMemberMediaUri: Uri =
            MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.id)

        override var imageFile = File(ImageUtils.getImagePathForModel(model, application))

        override fun hasValidData() = playlist.songsCount > 0

    }

}