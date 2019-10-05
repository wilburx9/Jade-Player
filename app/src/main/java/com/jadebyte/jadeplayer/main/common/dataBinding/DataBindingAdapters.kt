// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.dataBinding

import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
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
import com.jadebyte.jadeplayer.main.explore.RecentlyPlayed
import com.jadebyte.jadeplayer.main.genres.Genre
import com.jadebyte.jadeplayer.main.playback.MediaItemData
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
        GlideApp.with(view)
            .load(song?.album)
            .transform(
                MultiTransformation(centerCrop, circleCrop)
            )
            .placeholder(R.drawable.thumb_circular_default)
            .into(view)
    }

    @BindingAdapter("android:src")
    @JvmStatic
    fun setAlbumCover(view: ImageView, mediaItem: MediaItemData?) {
        GlideApp.with(view)
            .load(mediaItem)
            .transform(
                MultiTransformation(centerCrop, circleCrop)
            )
            .placeholder(R.drawable.thumb_circular_default)
            .into(view)
    }

    @BindingAdapter("android:src")
    @JvmStatic
    fun setAlbumCover(view: ImageView, item: RecentlyPlayed?) {
        GlideApp.with(view)
            .load(item)
            .transform(
                MultiTransformation(centerCrop, circleCrop)
            )
            .placeholder(R.drawable.thumb_circular_default)
            .into(view)
    }


    @BindingAdapter("mediaSrc")
    @JvmStatic
    fun setAlbumCoverCompat(view: ImageView, item: MediaItemData?) {
        GlideApp.with(view)
            .load(item)
            .transform(
                MultiTransformation(centerCrop, circleCrop, CircularTransparentCenter(.3F))
            )
            .placeholder(R.drawable.thumb_circular_default_hollow)
            .into(view)
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
            .load(playlist.modForViewWidth(view.measuredWidth))
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
            .load(playlist.modForViewWidth(view.measuredWidth))
            .transform(
                MultiTransformation(centerCrop, RoundedCorners(10))
            )
            .placeholder(R.drawable.thumb_default_short)
            .into(view)
    }

    @BindingAdapter("genreSrc")
    @JvmStatic
    fun setGenreSrc(view: ImageView, genre: Genre) {
        GlideApp.with(view)
            .load(genre)
            .transform(
                MultiTransformation(centerCrop, RoundedCorners(10))
            )
            .placeholder(R.drawable.thumb_default_short)
            .into(view)
    }

    @BindingAdapter("repeatSrc")
    @JvmStatic
    fun setRepeatModeSrc(view: ImageView, repeat: Int?) {
        val src = when (repeat) {
            PlaybackStateCompat.REPEAT_MODE_ALL -> R.drawable.ic_repeat_all
            PlaybackStateCompat.REPEAT_MODE_ONE -> R.drawable.ic_repeat_once
            else -> R.drawable.ic_repeat_none
        }
        view.setImageResource(src)
    }

    @BindingAdapter("shuffleSrc")
    @JvmStatic
    fun setShuffleModeSrc(view: ImageView, shuffle: Int?) {
        val src = if (shuffle == PlaybackStateCompat.SHUFFLE_MODE_NONE) {
            R.drawable.ic_shuffle_off
        } else {
            R.drawable.ic_shuffle_on
        }
        view.setImageResource(src)
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

    @BindingAdapter("enabled")
    @JvmStatic
    fun setEnabled(view: View, enabled: Boolean) {
        view.isEnabled = enabled
    }

}