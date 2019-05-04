// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.databinding.ItemPlaybackImageBinding
import com.jadebyte.jadeplayer.main.songs.Song
import timber.log.Timber

/**
 * Created by Wilberforce on 2019-05-02 at 00:25.
 */
class PlaybackAdapter(private var songs: List<Song>?) : PagerAdapter() {

    fun updateItems(items: List<Song>?) {
        Timber.i("updateItems: ${items!!.size}")
        this.songs = items
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return songs?.size ?: 0
    }

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view === any
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val binding =
            DataBindingUtil.inflate<ItemPlaybackImageBinding>(inflater, R.layout.item_playback_image, container, false)
        val song = songs?.get(position)
        binding.song = song
        binding.root.tag = song
        container.addView(binding.root)
        return binding.root
    }


    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(any as View)
    }
}
