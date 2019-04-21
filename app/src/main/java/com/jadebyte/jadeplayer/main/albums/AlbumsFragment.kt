// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.albums


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.main.common.view.BasePlayerFragment


class AlbumsFragment : BasePlayerFragment<Album>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)[AlbumsViewModel::class.java]
    }

    @SuppressLint("WrongConstant")
    override fun layoutManager(): RecyclerView.LayoutManager {
        return FlexboxLayoutManager(activity).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.SPACE_EVENLY
        }
    }

    override fun onItemClick(position: Int) {

    }

    override var itemLayoutId: Int = com.jadebyte.jadeplayer.R.layout.item_album
    override var viewModelVariableId: Int = BR.album
    override var navigationFragmentId: Int =
        com.jadebyte.jadeplayer.R.id.action_albumsFragment_to_navigationDialogFragment
    override var numberOfDataRes: Int = com.jadebyte.jadeplayer.R.plurals.numberOfAlbums
    override var titleRes: Int = com.jadebyte.jadeplayer.R.string.albums
    override var fadeInViewHolder: Boolean = true

}
