// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.databinding.ItemPlaybackImageBinding

/**
 * Created by Wilberforce on 2019-05-02 at 00:25.
 */
class PlaybackAdapter(private var items: List<MediaItemData>?) : PagerAdapter() {

    fun updateItems(items: List<MediaItemData>?) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return items?.size ?: 0
    }

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view === any
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val binding =
            DataBindingUtil.inflate<ItemPlaybackImageBinding>(inflater, R.layout.item_playback_image, container, false)
        val mediaItem = items?.get(position)
        binding.mediaItem = mediaItem
        binding.executePendingBindings()
        binding.root.tag = mediaItem?.id
        container.addView(binding.root)
        return binding.root
    }


    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(any as View)
    }
}
