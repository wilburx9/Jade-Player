// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.explore

import androidx.recyclerview.widget.RecyclerView
import com.jadebyte.jadeplayer.databinding.ItemExploreBinding
import com.jadebyte.jadeplayer.main.albums.Album

/**
 * Created by Wilberforce on 17/04/2019 at 03:52.
 */
class ExploreViewHolder(private val binding: ItemExploreBinding): RecyclerView.ViewHolder(binding.root) {
    init {
        itemView.setOnClickListener {  }
    }

    fun bind(album: Album?) {
        binding.album = album
        binding.executePendingBindings()
    }

}