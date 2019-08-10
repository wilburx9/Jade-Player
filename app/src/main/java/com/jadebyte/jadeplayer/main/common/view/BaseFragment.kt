// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.view

import androidx.fragment.app.Fragment
import com.jadebyte.jadeplayer.main.common.utils.Utils

/**
 * Created by Wilberforce on 2019-04-21 at 11:56.
 */
open class BaseFragment: Fragment() {
    fun isPermissionGranted(permission: String): Boolean = Utils.isPermissionGranted(permission, activity)
}