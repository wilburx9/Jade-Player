// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.songs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jadebyte.jadeplayer.databinding.ItemSongBinding

/**
 * Created by Wilberforce on 2019-04-20 at 23:40.
 */
class SongsAdapter (private var items: List<Song>): RecyclerView.Adapter<SongsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsViewHolder {
       val inflater = LayoutInflater.from(parent.context)
        val itemBinding  = ItemSongBinding.inflate(inflater, parent, false)
        return  SongsViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SongsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateItems(items: List<Song>) {
        this.items = items
        notifyDataSetChanged()
    }
}