// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.callbacks.MediaItemDataDiffCallback
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.view.BaseAdapter
import com.jadebyte.jadeplayer.main.common.view.BaseFragment
import kotlinx.android.synthetic.main.fragment_current_songs.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class CurrentSongsFragment : BaseFragment(), OnItemClickListener {

    private val viewModel: PlaybackViewModel by sharedViewModel()
    private var items = emptyList<MediaItemData>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_current_songs, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewViews()
        observeViewModel()
    }

    private fun setupViewViews() {
        currentRV.layoutManager = LinearLayoutManager(activity)
        val adapter = BaseAdapter(items, activity!!,R.layout.item_current, BR.mediaItem, this, null)
        currentRV.adapter = adapter
    }

    @Suppress("UNCHECKED_CAST")
    private fun observeViewModel() {
        viewModel.mediaItems.observe(viewLifecycleOwner, Observer {
            (currentRV.adapter as BaseAdapter<MediaItemData>).updateItems(it, MediaItemDataDiffCallback(items, it))
            items = it
        })

        viewModel.currentItem.observe(viewLifecycleOwner, Observer {
            val index = items.indexOf(it)
            if (index < 0) return@Observer
            currentRV.scrollToPosition(index)
        })
    }


    override fun onItemClick(position: Int, sharableView: View?) = viewModel.playMediaId(items[position].id)

}
