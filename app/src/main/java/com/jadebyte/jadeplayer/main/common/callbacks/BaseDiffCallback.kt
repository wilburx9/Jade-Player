// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.callbacks

import androidx.recyclerview.widget.DiffUtil
import com.jadebyte.jadeplayer.main.common.data.Model

open class BaseDiffCallback<T : Model>(private val oldList: List<T>, private val newList: List<T>) :
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]

    // We are returning 0 for partial update. For full update subclasses should return null
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? = 0
}
