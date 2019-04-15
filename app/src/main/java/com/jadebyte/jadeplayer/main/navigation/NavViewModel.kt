// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.navigation

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject


/**
 * Created by Wilberforce on 09/04/2019 at 17:15.
 */
class NavViewModel @Inject constructor(preferences: SharedPreferences) : ViewModel() {
    private val navRepository: NavRepository = NavRepository(preferences, 0)

    var navItems: LiveData<List<NavItem>>
        private set

    init {
        navItems = navRepository.liveItems
    }

    fun swap(list: List<NavItem>) = navRepository.swap(list)
}