// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.view

import androidx.fragment.app.DialogFragment
import com.jadebyte.jadeplayer.main.common.utils.Utils

open class BaseDialogFragment : DialogFragment() {
    fun isPermissionGranted(permission: String): Boolean = Utils.isPermissionGranted(permission, activity)
}