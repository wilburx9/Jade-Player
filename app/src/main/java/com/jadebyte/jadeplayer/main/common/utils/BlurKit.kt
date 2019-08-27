// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.Matrix
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View

/**
 * Created by Wilberforce on 12/04/2019 at 04:52.
 * Source: https://github.com/CameraKit/blurkit-android/blob/master/blurkit/src/main/java/io/alterac/blurkit/BlurKit.java
 */
class BlurKit {

    private fun blur(src: Bitmap, radius: Int): Bitmap {
        val input = Allocation.createFromBitmap(rs, src)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        try {
            script.setRadius(radius.toFloat())
            script.setInput(input)
            script.forEach(output)
            output.copyTo(src)
        } finally {
            input.destroy()
            output.destroy()
            script.destroy()
        }
        return src
    }

    fun blur(src: View, radius: Int): Bitmap {
        val bitmap = this.getBitmapForView(src)
        return this.blur(bitmap, radius)
    }

    fun fastBlur(src: View, radius: Int, downscaleFactor: Float): Bitmap {
        val bitmap = this.getBitmapForView(src, downscaleFactor)
        return this.blur(bitmap, radius)
    }

    private fun getBitmapForView(src: View, downscaleFactor: Float): Bitmap {
        val bitmap = Bitmap.createBitmap(
            (src.width.toFloat() * downscaleFactor).toInt(),
            (src.height.toFloat() * downscaleFactor).toInt(),
            Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        val matrix = Matrix()
        matrix.preScale(downscaleFactor, downscaleFactor)
        canvas.setMatrix(matrix)
        src.draw(canvas)
        return bitmap
    }

    private fun getBitmapForView(src: View): Bitmap {
        val bitmap = Bitmap.createBitmap(src.width, src.height, Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        src.draw(canvas)
        return bitmap
    }

    companion object {
        private val FULL_SCALE = 1.0f
        private var instance: BlurKit? = null
        private var rs: RenderScript? = null

        fun init(context: Context) {
            if (instance == null) {
                instance = BlurKit()
                rs = RenderScript.create(context.applicationContext)
            }
        }

        fun getInstance(): BlurKit {
            return instance ?: throw RuntimeException("BlurKit not initialized!")
        }
    }
}
