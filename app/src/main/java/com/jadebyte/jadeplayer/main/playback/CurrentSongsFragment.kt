// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.view.BaseAdapter
import com.jadebyte.jadeplayer.main.common.view.BaseFragment
import kotlinx.android.synthetic.main.fragment_current_songs.*


class CurrentSongsFragment : BaseFragment(), OnItemClickListener {

    private lateinit var viewModel: PlaybackViewModel
    private var items = emptyList<MediaItemData>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_current_songs, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!)[PlaybackViewModel::class.java]
        setupViewViews()
        observeViewModel()
    }

    private fun setupViewViews() {
        currentRV.layoutManager = LinearLayoutManager(activity)
        val adapter = BaseAdapter(items, activity!!, BR.mediaItem, R.layout.item_media, itemClickListener = this)
        currentRV.adapter = adapter
    }

    @Suppress("UNCHECKED_CAST")
    private fun observeViewModel() {
        viewModel.mediaItems.observe(viewLifecycleOwner, Observer {
            items = it
            (currentRV.adapter as BaseAdapter<MediaItemData>).updateItems(items)
        })
    }


    override fun onItemClick(position: Int, sharableView: View?) {
        viewModel.playMediaId(items[position].id)
    }

}
