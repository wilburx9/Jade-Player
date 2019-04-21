// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


/**
 * Created by Wilberforce on 2019-04-20 at 23:40.
 */
class BaseAdapter<T>(
    private var items: List<T>,
    private val context: Context,
    private val layoutId: Int,
    private val variableId: Int
) : RecyclerView.Adapter<BaseViewHolder<T>>() {

    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = DataBindingUtil.inflate<ViewDataBinding>(inflater, layoutId, parent, false)
        return BaseViewHolder(itemBinding, variableId)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(items[position])
        animateItem(position, holder)
    }

    fun updateItems(items: List<T>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<T>) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    private fun animateItem(position: Int, holder: RecyclerView.ViewHolder) {
        val animation = AnimationUtils.loadAnimation(
            context,
            if (position > lastPosition)
                com.jadebyte.jadeplayer.R.anim.up_from_bottom
            else
                com.jadebyte.jadeplayer.R.anim.down_from_top
        )
        holder.itemView.startAnimation(animation)
        lastPosition = position
    }

    override fun getItemViewType(position: Int): Int {
        return layoutId
    }
}
