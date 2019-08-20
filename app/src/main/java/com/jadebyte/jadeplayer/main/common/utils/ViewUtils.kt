// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.utils

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference


/**
 * Created by Wilberforce on 2019-05-19 at 01:52.
 */
object ViewUtils {
    @ColorInt
    fun resolveAndroidColorAttr(c: Context, @AttrRes colorAttrRes: Int): Int {
        val context = WeakReference(c).get()
        val resolvedAttr = resolveThemeAttr(context, colorAttrRes)
        val colorRes = if (resolvedAttr.resourceId != 0) resolvedAttr.resourceId else resolvedAttr.data
        return if (context == null) Color.parseColor("#b0b1b2") else ContextCompat.getColor(context, colorRes)
    }


    @ColorInt
    fun resolveColorAttr(c: Context, @AttrRes colorAttrRes: Int): Int {
        val context = WeakReference(c).get()
        val resolvedAttr = resolveThemeAttr(context, colorAttrRes)
        return if (context == null) Color.parseColor("#b0b1b2") else ContextCompat.getColor(context, resolvedAttr.data)
    }



    private fun resolveThemeAttr(context: Context?, @AttrRes attrRes: Int): TypedValue {
        val value = TypedValue()
        context?.theme?.resolveAttribute(attrRes, value, true)
        return value
    }
}