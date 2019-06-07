// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playlist


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.view.BaseAdapter
import kotlinx.android.synthetic.main.fragment_playlist.*

class PlaylistFragment : Fragment(), OnItemClickListener {

    private var items: List<Playlist> = emptyList()
    private lateinit var viewModel: PlaylistViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)[PlaylistViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
        navigationIcon.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_playlistFragment_to_navigationDialogFragment)
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun observeViewModel() {
        if (items.isEmpty()) {
            viewModel.init()
            viewModel.data.observe(viewLifecycleOwner, Observer {
                this.items = it
                (playlistRV.adapter as BaseAdapter<Playlist>).updateItems(it)
                updateViews()
            })
        } else {
            viewModel.data.value = items

        }
    }

    private fun updateViews() {
        if (items.isEmpty()) {
            playlistsGroup.visibility = View.GONE
            noPlaylistGroup.visibility = View.VISIBLE
        } else {
            noPlaylistGroup.visibility = View.GONE
            playlistsGroup.visibility = View.VISIBLE
            playlistsNum.text = resources.getQuantityString(R.plurals.numberOfPlaylists, items.count(), items.count())
        }
    }

    private fun setupViews() {
        playlistRV.adapter = BaseAdapter(items, activity!!, R.layout.item_playlist, BR.playlist, false, this)
        val layoutManager = LinearLayoutManager(activity)
        playlistRV.layoutManager = layoutManager
    }

    override fun onItemClick(position: Int, sharableView: View?) {

    }

}
