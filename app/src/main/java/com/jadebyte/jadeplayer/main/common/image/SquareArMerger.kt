// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.ThumbnailUtils
import com.jadebyte.jadeplayer.common.triangle


object BitmapMerger {
    fun merge(
        bitmaps: List<Bitmap>, resultWidth: Float, resultHeight: Float
    ): Bitmap {

        if (bitmaps.size < 2) {
            return bitmaps[0]
        }


        val bgBitmap = Bitmap.createBitmap(resultWidth.toInt(), resultHeight.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bgBitmap)
        val bitmapsCount = bitmaps.size

        if (bitmapsCount < 4) {
            bitmaps.take(2).forEachIndexed { index, bitmap ->
                val b = ThumbnailUtils.extractThumbnail(bitmap, resultWidth.toInt(), resultHeight.toInt())
                val triangle = b.triangle(index == 0)
                canvas.drawBitmap(triangle, 0F, 0F, null)
            }

        } else {
            val halfWidth = resultWidth.toInt() / 2
            val halfHeight = resultHeight.toInt() / 2

            bitmaps.take(4).forEachIndexed { index, bitmap ->
                val b = ThumbnailUtils.extractThumbnail(bitmap, halfWidth, halfHeight)
                val left = if (index == 0 || index == 2) 0f else resultHeight / 2
                val top = if (index < 2) 0f else resultHeight / 2
                canvas.drawBitmap(b, left, top, null)
            }

        }
        return bgBitmap
    }
}