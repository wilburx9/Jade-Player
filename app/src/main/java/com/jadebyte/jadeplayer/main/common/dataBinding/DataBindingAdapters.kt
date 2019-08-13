// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.dataBinding

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.GlideApp
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.artists.Artist
import com.jadebyte.jadeplayer.main.common.image.CircularTransparentCenter
import com.jadebyte.jadeplayer.main.playlist.Playlist
import com.jadebyte.jadeplayer.main.songs.Song


/**
 * Created by Wilberforce on 10/04/2019 at 19:26.
 */
object DataBindingAdapters {

    @JvmStatic val centerCrop = CenterCrop()
    @JvmStatic val circleCrop = CircleCrop()

    @BindingAdapter("android:src")
    @JvmStatic
    fun setAlbumCover(view: ImageView, song: Song?) {
        if (song != null) {
            GlideApp.with(view)
                .load(song.album)
                .transform(
                    MultiTransformation(centerCrop, circleCrop)
                )
                .placeholder(R.drawable.thumb_circular_default)
                .into(view)
        }
    }


    @BindingAdapter("songSrc")
    @JvmStatic
    fun setAlbumCoverCompat(view: ImageView, song: Song?) {
        if (song != null) {
            GlideApp.with(view)
                .load(song.album)
                .transform(
                    MultiTransformation(centerCrop, circleCrop, CircularTransparentCenter(.3F))
                )
                .placeholder(R.drawable.thumb_circular_default_hollow)
                .into(view)
        }


    }

    @BindingAdapter("android:src")
    @JvmStatic
    fun setArtistAvatar(view: ImageView, artist: Artist) {
        GlideApp.with(view)
            .load(artist)
            .transform(
                MultiTransformation(centerCrop, circleCrop)
            )
            .placeholder(R.drawable.thumb_circular_default)
            .into(view)
    }

    @BindingAdapter("android:src")
    @JvmStatic
    fun setAlbumCover(view: ImageView, album: Album) {
        GlideApp.with(view)
            .load(album)
            .transform(
                MultiTransformation(centerCrop, RoundedCorners(10))
            )
            .placeholder(R.drawable.thumb_default)
            .into(view)
    }

    @BindingAdapter("artistSrc")
    @JvmStatic
    fun setAlbumSrc(view: ImageView, album: Album) {
        GlideApp.with(view)
            .load(album)
            .transform(
                MultiTransformation(centerCrop, RoundedCorners(10))
            )
            .placeholder(R.drawable.thumb_default_short)
            .into(view)
    }

    @BindingAdapter("android:src")
    @JvmStatic
    fun setPlaylistCover(view: ImageView, playlist: Playlist) {
        GlideApp.with(view)
            .load(playlist)
            .transform(
                MultiTransformation(centerCrop, circleCrop)
            )
            .placeholder(R.drawable.thumb_circular_default)
            .into(view)
    }

    @BindingAdapter("playlistSrc")
    @JvmStatic
    fun setPlaylistSrc(view: ImageView, playlist: Playlist) {
        GlideApp.with(view)
            .load(playlist)
            .transform(
                MultiTransformation(centerCrop, RoundedCorners(10))
            )
            .placeholder(R.drawable.thumb_default_short)
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

    @BindingAdapter("android:textColor")
    @JvmStatic
    fun setTextColor(view: TextView, @ColorRes color: Int) {
        view.setTextColor(ContextCompat.getColor(view.context, color))
    }

    @BindingAdapter("enabled")
    @JvmStatic
    fun setEnabled(view: View, enabled: Boolean) {
        view.isEnabled = enabled
    }

}