// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.view

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import kotlinx.android.synthetic.main.item_song.view.*

/**
 * Created by Wilberforce on 2019-04-20 at 23:37.
 */
class BaseViewHolder<T>(
    private val binding: ViewDataBinding,
    private val variableId: Int,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    init {
        itemView.setOnClickListener(this)
        itemView.moreOptions?.setOnClickListener(this)
    }


    fun bind(item: T) {
        binding.setVariable(variableId, item)
        binding.executePendingBindings()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.container -> itemClickListener.onItemClick(adapterPosition, itemView.albumArt)
        }
    }
}