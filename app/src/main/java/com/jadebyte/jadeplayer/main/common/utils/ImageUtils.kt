// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.utils

import android.content.ContentUris
import android.content.Context
import android.graphics.*
import android.media.ThumbnailUtils
import android.net.Uri
import androidx.core.content.ContextCompat
import com.jadebyte.jadeplayer.main.common.data.Model
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*
import kotlin.math.min


/**
 * Created by Wilberforce on 16/04/2019 at 01:31.
 */
object ImageUtils {

    /**
     * Merges bitmaps
     *
     * Only 2 and 4 bitmaps are supported for the interim.
     * For 2 bitmaps, they're triangle cropped and a collage of them is generated
     *
     * For 4 bitmaps, a 2 X 2 collage of the them
     *
     * @param bitmaps the list of bitmaps to merge together
     * @param resultWidth width of the merged bitmap
     * @param resultHeight height of the merged bitmap
     * @return the merged bitmap
     */
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
                val triangle = triangleCrop(b, index == 0)
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

    // get the album cover URi from the album Id
    fun getAlbumArtUri(albumId: Long): Uri {
        val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(sArtworkUri, albumId)
    }

    /**
     * If necessary, resizes the image located in imagePath and then returns the path for the scaled
     * image.
     *
     * If no resizing is needed, returns the path for the original image.   Resisizes an image with given path
     * @param imagePath the file path of the image
     * @param maxWidth the max attainable width of the new resized image
     * @param maxHeight the max attainable height of the new resized image
     * @param resultPath The path to save the resultant image
     *
     * Source: https://raw.githubusercontent.com/flutter/plugins/master/packages/image_picker/android/src/main/java/io/flutter/plugins/imagepicker/ImageResizer.java
     */
    fun resizeImageIfNeeded(
        imagePath: String, maxWidth: Double?, maxHeight: Double?, imageQuality: Int,
        resultPath: String
    ): String? {
        val shouldScale = maxWidth != null || maxHeight != null || imageQuality > -1 && imageQuality < 101

        if (!shouldScale) {
            return imagePath
        }

        return try {
            val scaledImage = resizedImage(imagePath, maxWidth, maxHeight, imageQuality, resultPath)

            scaledImage.path
        } catch (e: IOException) {
            Timber.e("Could  not resize image in $imagePath. Error = $e")
            null
        }

    }

    /**
     * Resisizes an image with given path
     * @param path the file path of the image
     * @param maxWidth the max attainable width of the new resized image
     * @param maxHeight the max attainable height of the new resized image
     * @param resultPath The path to save the resultant image
     *
     *
     * Original source: https://raw.githubusercontent.com/flutter/plugins/master/packages/image_picker/android/src/main/java/io/flutter/plugins/imagepicker/ImageResizer.java
     */
    @Throws(IOException::class)
    private fun resizedImage(
        path: String,
        maxWidth: Double?,
        maxHeight: Double?,
        imageQuality: Int,
        resultPath: String
    ):
            File {
        var quality = imageQuality
        val bmp = BitmapFactory.decodeFile(path)
        val originalWidth = bmp.width * 1.0
        val originalHeight = bmp.height * 1.0

        if (quality < 0 || quality > 100) {
            quality = 100
        }

        val hasMaxWidth = maxWidth != null
        val hasMaxHeight = maxHeight != null

        var width: Double? = if (hasMaxWidth) min(originalWidth, maxWidth!!) else originalWidth
        var height: Double? = if (hasMaxHeight) min(originalHeight, maxHeight!!) else originalHeight

        if (hasMaxHeight && hasMaxWidth) {
            val shouldDownscaleWidth = hasMaxWidth && maxWidth!! < originalWidth
            val shouldDownscaleHeight = hasMaxHeight && maxHeight!! < originalHeight
            val shouldDownscale = shouldDownscaleWidth || shouldDownscaleHeight

            if (shouldDownscale) {
                val downscaledWidth = height!! / originalHeight * originalWidth
                val downscaledHeight = width!! / originalWidth * originalHeight

                if (width < height) {
                    if (!hasMaxWidth) {
                        width = downscaledWidth
                    } else {
                        height = downscaledHeight
                    }
                } else if (height < width) {
                    if (!hasMaxHeight) {
                        height = downscaledHeight
                    } else {
                        width = downscaledWidth
                    }
                } else {
                    if (originalWidth < originalHeight) {
                        width = downscaledWidth
                    } else if (originalHeight < originalWidth) {
                        height = downscaledHeight
                    }
                }
            }
        }

        val scaledBmp = Bitmap.createScaledBitmap(bmp, width!!.toInt(), height!!.toInt(), false)
        val outputStream = ByteArrayOutputStream()
        val saveAsPNG = bmp.hasAlpha()
        if (saveAsPNG) {
            Timber.i("compressing is not supported for type PNG. Returning the image with original quality");
        }
        scaledBmp.compress(
            if (saveAsPNG) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG,
            quality,
            outputStream
        )

        val imageFile = File(resultPath)
        if (!imageFile.exists()) {
            imageFile.parentFile.mkdirs()
            imageFile.createNewFile()
        }
        val fileOutput = FileOutputStream(imageFile)
        fileOutput.write(outputStream.toByteArray())
        fileOutput.close()
        return imageFile
    }

    fun getImagePathForModel(model: Model, c: Context?): String? {
        val context = WeakReference<Context>(c).get()
        if (context != null) {
            return "${context.filesDir}/images/${model.javaClass.name.toLowerCase(Locale.ROOT)}/${model.id}"
        }
        return null
    }

    /**
     * Crops a triangle from the bitmap
     * @param src the bitmap to crop
     * @param isLeft direction of the right-angle of the triangle.
     * @return the cropped bitmap in a triangle shape
     */
    private fun triangleCrop(src: Bitmap, isLeft: Boolean): Bitmap {
        val output = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.BLACK

        val path = if (isLeft) Path().apply {
            moveTo(0F, 0F)
            lineTo(src.width.toFloat(), 0F)
            lineTo(0F, src.height.toFloat())
            close()
        } else Path().apply {
            moveTo(0F, src.height.toFloat())
            lineTo(src.width.toFloat(), 0F)
            lineTo(src.height.toFloat(), src.height.toFloat())
        }
        path.fillType = Path.FillType.EVEN_ODD

        canvas.drawPath(path, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(src, 0F, 0F, paint)
        return output
    }

    fun getBitmapFromVectorDrawable(c: Context, drawableId: Int): Bitmap? {
        val context = WeakReference<Context>(c).get() ?: return null
        val drawable = ContextCompat.getDrawable(context, drawableId)

        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }


}