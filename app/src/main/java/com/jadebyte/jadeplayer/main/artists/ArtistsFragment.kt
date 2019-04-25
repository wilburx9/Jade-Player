// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.artists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.view.BaseAdapter
import kotlinx.android.synthetic.main.fragment_artists.*
import kotlinx.android.synthetic.main.fragment_explore.navigationIcon

class ArtistsFragment : Fragment(), OnItemClickListener {

    private var items: List<Artist> = emptyList()
    private lateinit var viewModel: ArtistsViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_artists, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this)[ArtistsViewModel::class.java]
        viewModel.init()
        setupRecyclerView()
        observeViewModel()
        navigationIcon.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                        R.id.action_artistsFragment_to_navigationDialogFragment
                )
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun observeViewModel() {
        if (items.isEmpty()) {
            viewModel.data.observe(viewLifecycleOwner, Observer {
                this.items = it
                (artistsRV.adapter as BaseAdapter<Artist>).updateItems(it)
            })
        } else {
            viewModel.data.value = items

        }
    }

    private fun setupRecyclerView() {
        val adapter = BaseAdapter(items, activity!!, R.layout.item_artist, BR.artist, false, this)
        artistsRV.adapter = adapter

        val layoutManager = LinearLayoutManager(activity)
        artistsRV.layoutManager = layoutManager
    }

    override fun onItemClick(position: Int, art: ImageView?) {

    }


}
