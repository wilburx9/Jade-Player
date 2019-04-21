// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jadebyte.jadeplayer.databinding.ItemExploreBinding
import com.jadebyte.jadeplayer.main.albums.Album

/**
 * Created by Wilberforce on 17/04/2019 at 03:50.
 */
class ExploreAdapter (private var items: List<Album>): RecyclerView.Adapter<ExploreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemExploreBinding.inflate(inflater, parent, false)
        return ExploreViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ExploreViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateItems(items: List<Album>) {
        this.items = items
        notifyDataSetChanged()
    }
}