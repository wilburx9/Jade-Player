// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.callbacks

import com.jadebyte.jadeplayer.main.navigation.NavigationDialogFragment

/**
 * Created by Wilberforce on 2019-04-24 at 22:08.
 */
interface OnNavigationItemClickListener {
    /**
     * Called when an item of [NavigationDialogFragment] is clicked
     *  @param itemId the id of the item that was clicked
     */
    fun onNavigationItemClicked(itemId: String)
}