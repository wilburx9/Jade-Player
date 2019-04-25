// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.network.image

import com.bumptech.glide.load.model.ModelLoaderFactory
import java.io.InputStream

/**
 * Created by Wilberforce on 2019-04-25 at 06:48.
 */
abstract class BaseModelLoaderFactory<M> : ModelLoaderFactory<M, InputStream> {
    override fun teardown() {}
}