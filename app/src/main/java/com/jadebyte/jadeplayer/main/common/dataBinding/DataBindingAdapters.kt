// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.dataBinding

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.GlideApp
import com.jadebyte.jadeplayer.main.songs.Song

/**
 * Created by Wilberforce on 10/04/2019 at 19:26.
 */
object DataBindingAdapters {

    @BindingAdapter("android:src")
    @JvmStatic
    fun setImageUri(view: ImageView, song: Song) {
        GlideApp.with(view)
            .load(song)
            .transform(
                MultiTransformation(CenterCrop(), RoundedCorners(10))
            )
            .placeholder(R.drawable.thumb_default)
            .into(view)
    }

    @BindingAdapter("android:src")
    @JvmStatic
    fun setImageResource(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }

    @BindingAdapter("android:background")
    @JvmStatic
    fun setBackgroundResource(view: View, @DrawableRes resource: Int) {
        view.setBackgroundResource(resource)
    }


}