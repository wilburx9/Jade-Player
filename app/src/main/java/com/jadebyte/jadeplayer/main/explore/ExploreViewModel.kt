// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.explore

import android.app.Application
import com.jadebyte.jadeplayer.main.albums.AlbumsViewModel

/**
 * Created by Wilberforce on 17/04/2019 at 04:01.
 */
class ExploreViewModel(application: Application) : AlbumsViewModel(application) {

    override var sortOrder: String? = "RANDOM() LIMIT 5"

}
