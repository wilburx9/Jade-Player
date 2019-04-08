// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.common;

/**
 * Created by Wilberforce on 08/04/2019 at 20:22.
 * Original Source: https://gist.github.com/rocboronat/65b1187a9fca9eabfebb5121d818a3c4
 */

import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector


object PermissionGranter {

    private const val PERMISSIONS_DIALOG_DELAY = 3000

    internal const val DENY_BUTTON_INDEX = 0
    internal const val GRANT_BUTTON_INDEX = 1

    @IntDef(DENY_BUTTON_INDEX, GRANT_BUTTON_INDEX)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Action


    fun allowPermissionsIfNeeded(permissionNeeded: String, @Action action: Int) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !hasNeededPermission(permissionNeeded)) {
                sleep(PERMISSIONS_DIALOG_DELAY.toLong())
                val device = UiDevice.getInstance(getInstrumentation())
                val permissionButton = device.findObject(
                    UiSelector()
                        .clickable(true)
                        .checkable(false)
                        .index(action)
                )
                if (permissionButton.exists()) {
                    permissionButton.click()
                }
            }
        } catch (e: UiObjectNotFoundException) {
            println("There is no permissions dialog to interact with")
        }

    }

    private fun hasNeededPermission(permissionNeeded: String): Boolean {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val permissionStatus = ContextCompat.checkSelfPermission(context, permissionNeeded)
        return permissionStatus == PackageManager.PERMISSION_GRANTED
    }

    private fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            throw RuntimeException("Cannot execute Thread.sleep()")
        }

    }
}
