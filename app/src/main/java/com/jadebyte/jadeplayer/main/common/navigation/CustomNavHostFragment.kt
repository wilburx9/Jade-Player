// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.navigation

import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.plusAssign


/**
 * Created by Wilberforce on 09/04/2019 at 23:47.
 * A [NavHostFragment] who supports navigation to [DialogFragment]s.
 * Source: https://gist.github.com/matpag/74ce059d590ff571c8240428b274c8c5
 */
class CustomNavHostFragment : NavHostFragment() {
    override fun createFragmentNavigator(): Navigator<out FragmentNavigator.Destination> {
        navController.navigatorProvider += DialogFragmentNavigator(childFragmentManager)
        return super.createFragmentNavigator()
    }
}