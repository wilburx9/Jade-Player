// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jadebyte.jadeplayer.databinding.ItemNavBinding
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.dragSwipe.OnStartDragListener


/**
 * Created by Wilberforce on 09/04/2019 at 16:39.
 */
class NavAdapter(
    private var items: List<NavItem>?,
    private val dragStartListener: OnStartDragListener,
    private val onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<NavViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemNavBinding.inflate(inflater, parent, false)
        return NavViewHolder(itemBinding, dragStartListener, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: NavViewHolder, position: Int) {
        viewHolder.bind(items?.get(position))
    }

    fun updateItems(items: List<NavItem>, fromPosition: Int? = null, toPosition: Int? = null) {
        this.items = items
        if (fromPosition != null && toPosition != null)
            notifyItemMoved(fromPosition, toPosition)
        else notifyDataSetChanged()
    }

}