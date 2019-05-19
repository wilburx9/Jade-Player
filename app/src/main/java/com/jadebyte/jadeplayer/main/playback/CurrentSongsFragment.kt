// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.databinding.ItemCurrentBinding
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.view.BaseFragment
import com.jadebyte.jadeplayer.main.common.view.BaseViewHolder
import com.jadebyte.jadeplayer.main.songs.Song
import kotlinx.android.synthetic.main.fragment_current_songs.*


class CurrentSongsFragment : BaseFragment(), OnItemClickListener {

    private lateinit var viewModel: PlaybackViewModel
    private var items = emptyList<Song>()


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
        val adapter = Adapter(items, itemClickListener = this)
        currentRV.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.mediatorLiveData.observe(viewLifecycleOwner, dataObserver)
    }

    private val dataObserver = Observer(this::updateViews)

    override fun onItemClick(position: Int, sharableView: View?) {
        viewModel.updateIndexOfPlayingSong(position)
    }

    private fun updateViews(result: Any) {
        val adapter = currentRV.adapter as Adapter
        if (result is Int) {
            adapter.updateItems(items, result)
        } else {
            @Suppress("UNCHECKED_CAST")
            items = result as List<Song>
            adapter.updateItems(items)
        }
    }

    override fun onDestroyView() {
        viewModel.mediatorLiveData.removeObserver(dataObserver)
        super.onDestroyView()
    }

}


private class Adapter(
    private var items: List<Song>,
    private var indexOfPlayingSong: Int = 0,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<BaseViewHolder<Song>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Song> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCurrentBinding.inflate(inflater, parent, false)
        return BaseViewHolder(binding, BR.song, itemClickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Song>, position: Int) {
        holder.bind(items[position])
    }

    fun updateItems(items: List<Song>, indexOfPlayingSong: Int? = null) {
        this.items = items
        if (indexOfPlayingSong != null && this.items.isNotEmpty()) {
            // The current song is no longer current
            this.items[this.indexOfPlayingSong].isCurrent = false
            notifyItemChanged(this.indexOfPlayingSong)

            // We have a new current song
            this.items[indexOfPlayingSong].isCurrent = true
            notifyItemChanged(indexOfPlayingSong)
            this.indexOfPlayingSong = indexOfPlayingSong
        } else {
            notifyDataSetChanged()
        }
    }

}
