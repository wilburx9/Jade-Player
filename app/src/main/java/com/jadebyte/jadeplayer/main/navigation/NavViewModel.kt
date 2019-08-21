// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.navigation

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel


/**
 * Created by Wilberforce on 09/04/2019 at 17:15.
 */
class NavViewModel(private val preferences: SharedPreferences) : ViewModel() {
    private var navRepository: NavRepository? = null
    var navItems: LiveData<List<NavItem>>? = null
        private set

    fun init(origin: Int?) {
        if (this.navItems != null) {
            return
        }

        navRepository = NavRepository(origin, preferences)
        navItems = navRepository?.liveItems
    }

    fun swap(list: List<NavItem>) = navRepository?.swap(list)
}