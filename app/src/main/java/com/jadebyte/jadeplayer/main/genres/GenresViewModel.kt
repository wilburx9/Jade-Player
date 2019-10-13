// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.genres

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.common.data.MediaStoreRepository
import com.jadebyte.jadeplayer.main.common.view.BaseMediaStoreViewModel

class GenresViewModel(application: Application) : BaseMediaStoreViewModel<Genre>(application) {

    override var repository: MediaStoreRepository<Genre> = GenresRepository(application)

    override var sortOrder: String? = "${MediaStore.Audio.Genres.NAME} COLLATE NOCASE ASC"

    override var uri: Uri = baseGenreUri

    // This selection will only return genres that has at least one song.
    override var selection: String? =
        "_id in (select genre_id from audio_genres_map where audio_id in (select _id from audio_meta where is_music != 0))"

    override var projection: Array<String>? = baseGenreProjection
}

val baseGenreProjection = arrayOf(
    MediaStore.Audio.Genres._ID,
    MediaStore.Audio.Genres.NAME
)

val  baseGenreUri: Uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI
