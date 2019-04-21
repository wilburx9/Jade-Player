// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.songs


import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.view.BasePlayerFragment

class SongsFragment : BasePlayerFragment<Song>() {
    override var itemLayoutId: Int = R.layout.item_song
    override var viewModelVariableId: Int = BR.song
    override var navigationFragmentId: Int =  R.id.action_songsFragment_to_navigationDialogFragment
    override var numberOfDataRes: Int = R.plurals.numberOfTracks
    override var titleRes: Int = R.string.songs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)[SongsViewModel::class.java]
    }

}
