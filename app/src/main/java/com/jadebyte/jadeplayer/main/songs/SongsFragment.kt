// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.songs


import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.view.BasePlayerFragment

class SongsFragment : BasePlayerFragment<Song>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)[SongsViewModel::class.java]
    }

    override fun onItemClick(position: Int, sharableView: View?) {
        playbackViewModel.playAll(items[position].id.toString())
    }

    override fun onOverflowMenuClick(position: Int) {
        val action =
            SongsFragmentDirections.actionSongsFragmentToSongsMenuBottomSheetDialogFragment(mediaId = items[position].id)
        findNavController().navigate(action)
    }

    override var itemLayoutId: Int = R.layout.item_song
    override var viewModelVariableId: Int = BR.song
    override var navigationFragmentId: Int = R.id.action_songsFragment_to_navigationDialogFragment
    override var numberOfDataRes: Int = R.plurals.numberOfSongs
    override var titleRes: Int = R.string.songs

}
