// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.explore


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.view.BaseAdapter
import kotlinx.android.synthetic.main.fragment_explore.*


class ExploreFragment : Fragment(), OnItemClickListener {

    private var albums: List<Album> = emptyList()
    private lateinit var viewModel: ExploreViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)[ExploreViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        navigationIcon.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                R.id.action_exploreFragment_to_navigationDialogFragment
            )
        )

    }

    @Suppress("UNCHECKED_CAST")
    private fun observeViewModel() {
        if (albums.isEmpty()) {
            viewModel.init()
            viewModel.items.observe(viewLifecycleOwner, Observer {
                this.albums = it
                (randomAlbumsRV.adapter as BaseAdapter<Album>).updateItems(it)
            })
        } else {
            viewModel.overrideCurrentItems(albums)

        }
    }

    private fun setupRecyclerView() {
        // Important!! See https://rubensousa.com/2019/08/16/nested_recyclerview_part1/ for improving nested 
        // scrolling in multiple fragments
        val adapter = BaseAdapter(albums, activity!!, R.layout.item_album, BR.album, true, this, longClick = true)
        randomAlbumsRV.adapter = adapter

        val layoutManager = LinearLayoutManager(activity, LinearLayout.HORIZONTAL, false)
        randomAlbumsRV.layoutManager = layoutManager
    }

    override fun onItemClick(position: Int, sharableView: View?) {
        val transitionName = ViewCompat.getTransitionName(sharableView!!)!!
        val extras = FragmentNavigator.Extras.Builder()
            .addSharedElement(sharableView, transitionName)
            .build()
        val action =
            ExploreFragmentDirections.actionExploreFragmentToAlbumSongsFragment(albums[position], transitionName)
        findNavController().navigate(action, extras)
    }

    override fun onItemLongClick(position: Int) {
        val action = ExploreFragmentDirections.actionExploreFragmentToAlbumsMenuBottomSheetDialogFragment(album = albums[0])
        findNavController().navigate(action)
    }
}
