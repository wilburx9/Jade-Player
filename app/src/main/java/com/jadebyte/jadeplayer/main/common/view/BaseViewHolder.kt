// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.view

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_song.view.*

/**
 * Created by Wilberforce on 2019-04-20 at 23:37.
 */
class BaseViewHolder<T>(private val binding: ViewDataBinding, private val variableId: Int) :
    RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    init {
        itemView.setOnClickListener(this)
        itemView.moreOptions.setOnClickListener(this)
    }


    fun bind(item: T) {
        binding.setVariable(variableId, item)
        binding.executePendingBindings()
    }

    override fun onClick(v: View?) {

    }
}