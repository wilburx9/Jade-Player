// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.common

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.jadebyte.jadeplayer.main.common.utils.Utils

/**
 * Created by Wilberforce on 08/04/2019 at 21:23.
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {
    fun isPermissionGranted(permission: String): Boolean = Utils.isPermissionGranted(permission, this)
}