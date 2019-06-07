// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.view.BaseFullscreenDialogFragment

class NewPlaylistDialogFragment : BaseFullscreenDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_playlist_dialog, container, false)
    }


}
