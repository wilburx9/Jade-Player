// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.callbacks

import android.view.View

/**
 * Created by Wilberforce on 2019-04-21 at 03:35.
 */
interface OnItemClickListener {
    fun onItemClick(position: Int, sharableView: View? = null)

    fun onOverflowMenuClick(position: Int) {}

    fun onItemLongClick(position: Int) {}
}