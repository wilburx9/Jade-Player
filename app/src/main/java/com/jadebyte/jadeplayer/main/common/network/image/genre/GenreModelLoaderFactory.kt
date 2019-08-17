// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image.genre

import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.jadebyte.jadeplayer.main.genres.Genre
import java.io.InputStream


/**
 * Created by Wilberforce on 2019-08-17 at 23:28.
 */
class GenreModelLoaderFactory : ModelLoaderFactory<Genre, InputStream> {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<Genre, InputStream> {
        return GenreModelLoader()
    }

    override fun teardown() {}

}