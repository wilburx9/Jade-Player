// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.songs

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.jadebyte.jadeplayer.databinding.ItemSongBinding
import kotlinx.android.synthetic.main.item_song.view.*

/**
 * Created by Wilberforce on 2019-04-20 at 23:37.
 */
class SongsViewHolder(private val binding: ItemSongBinding): RecyclerView.ViewHolder(binding.root),
    View.OnClickListener {

    init {
        itemView.setOnClickListener(this)
        itemView.moreOptions.setOnClickListener(this)
    }


    fun bind(song: Song) {
        binding.song = song
        binding.executePendingBindings()
    }

    override fun onClick(v: View?) {

    }
}