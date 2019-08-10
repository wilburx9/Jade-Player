// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.utils

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import kotlin.math.min


/**
 * Created by Wilberforce on 16/04/2019 at 01:31.
 */
object ImageUtils {

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

    fun getImagePathForPlaylist(playlistId: Long, c: Context?): String? {
        val context = WeakReference<Context>(c).get()
        if (context != null) {
            return "${context.filesDir}/images/playlist/$playlistId"
        }
        return null
    }
}