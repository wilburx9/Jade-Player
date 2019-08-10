// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference


/**
 * Created by Wilberforce on 2019-04-21 at 23:47.
 */
object Utils {

    /**
     * Removes the the first three character oif [MediaStore.Audio.Media.TRACK]
     */
    fun getTrackNumber(number: Int): String {
        val num = number.toString()
        return if (num.length >= 4) num.drop(3) else num
    }

    fun vibrateAfterAction(c: Context) {
        val context = WeakReference(c).get()
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        }
    }

    fun isPermissionGranted(permission: String, c: Context?): Boolean {
        val context = WeakReference(c).get()
        return context?.let { ContextCompat.checkSelfPermission(it, permission) } == PackageManager.PERMISSION_GRANTED
    }


}