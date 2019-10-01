// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.view

import android.view.View
import android.widget.CheckBox
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener

/**
 * Created by Wilberforce on 2019-04-20 at 23:37.
 */
class BaseViewHolder<T>(
    private val binding: ViewDataBinding,
    private val variableId: Int,
    private val itemClickListener: OnItemClickListener? = null,
    longClick: Boolean = false
) :
    RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

    init {
        if (itemClickListener != null) {
            itemView.setOnClickListener(this)
            if (longClick) itemView.setOnLongClickListener(this)
            itemView.findViewById<View>(R.id.moreOptions)?.setOnClickListener(this)
            itemView.findViewById<CheckBox>(R.id.checkbox)?.setOnClickListener(this)
        }
    }


    fun bind(item: T) {
        binding.setVariable(variableId, item)
        binding.executePendingBindings()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.container, R.id.checkbox -> itemClickListener?.onItemClick(
                adapterPosition,
                itemView.findViewById(R.id.sharableView)
            )
            R.id.moreOptions -> itemClickListener?.onOverflowMenuClick(adapterPosition)
        }
    }

    override fun onLongClick(v: View?): Boolean {
        return when (v?.id) {
            R.id.container -> {
                itemClickListener?.onItemLongClick(adapterPosition)
                true
            }
            else -> false
        }
    }
}